package com.luna.chatroom.ws;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.luna.chatroom.constant.UserConstant;
import com.luna.chatroom.utils.MessageUtils;
import com.luna.chatroom.pojo.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author luna
 */
@Slf4j
@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfigurator.class)
@Component
public class ChatEndPoint {
    /**
     * 保存所有socket连接用户
     */
    public static Map<String, ChatEndPoint> onLineUsers = new ConcurrentHashMap<>();

    /**
     * 声明一个session对象，通过该对象可以发送消息给指定用户，不能设置为静态，每个ChatEndPoint有一个session才能区分.(websocket的session)
     */
    private Session session;

    public Session getSession() {
        return session;
    }

    /**
     * 保存当前登录浏览器的用户
     */
    private HttpSession httpSession;

    /**
     * 建立连接时发送系统广播
     *
     * @param session
     * @param config
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.httpSession = httpSession;
        String username = (String) httpSession.getAttribute(UserConstant.SESSION_KEY);
        log.info("上线用户名称：" + username);
        onLineUsers.put(username, this);
        sysSendMessage(username, "上线了");
    }

    /**
     * 获取除自己外当前登录的用户
     *
     * @return
     */
    public static List<String> getNamesExcludeMe(String username) {
        return onLineUsers.keySet().stream().filter(name -> !Objects.equals(name, username)).collect(Collectors.toList());
    }

    /**
     * 发送系统消息
     *
     * @param message
     */
    private void broadcastAllUsersExcludeOne(String username, String message) {
        List<String> namesExcludeMe = getNamesExcludeMe(username);
        sendMessage(username, namesExcludeMe, message);
    }

    private void broadcastAllUsersExcludeOne(String username, List<String> toNames, String message) {
        sendMessage(username, toNames, message);
    }

    private void sendMessage(String username, List<String> toNames, String message) {
        try {
            for (String name : toNames) {
                if (!Objects.equals(username, name)) {
                    ChatEndPoint chatEndPoint = onLineUsers.get(name);
                    chatEndPoint.session.getBasicRemote().sendText(message);
                }
            }
            log.info("发送消息{}", message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发生错误时触发
     *
     * @param error
     */
    @OnError
    public void onError(Throwable error) {
        onLineUsers.forEach((k, v) -> {
            if (v.getSession().getId().equals(session.getId())) {
                onLineUsers.remove(k);
                log.error("客户端:【{}】发生异常", k);
                error.printStackTrace();
            }
        });
    }

    /**
     * 用户之间的信息发送
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            String username = (String) httpSession.getAttribute(UserConstant.SESSION_KEY);
            Message msg = JSON.parseObject(message, Message.class);
            normalSendMessage(username, Lists.newArrayList(msg.getToName()), msg.getMessage());
            log.info(username + "向" + msg.getToName() + "发送的消息：" + msg.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用户断开连接的断后操作
     */
    @OnClose
    public void onClose() {
        String username = (String) httpSession.getAttribute(UserConstant.SESSION_KEY);
        log.info("离线用户：" + username);
        onLineUsers.remove(username);
        sysSendMessage(username, "下线了");
    }

    public void sysSendMessage(String username, Object message) {
        String msg = MessageUtils.getMessage(true, username, getNamesExcludeMe(username), message);
        broadcastAllUsersExcludeOne(username, msg);
    }

    public void normalSendMessage(String username, List<String> toNames, Object message) {
        String msg = MessageUtils.getMessage(false, username, toNames, message);
        broadcastAllUsersExcludeOne(username,toNames, msg);
    }


}
