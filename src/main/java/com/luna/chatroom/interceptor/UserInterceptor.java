package com.luna.chatroom.interceptor;


import com.luna.chatroom.constant.UserConstant;
import com.luna.chatroom.ws.ChatEndPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author luna
 */
@Component
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession httpSession = request.getSession();
        String username = (String) httpSession.getAttribute(UserConstant.SESSION_KEY);
        // 存在
        if (username == null) {
            log.info("在线的用户：【{}】", ChatEndPoint.onLineUsers.keySet());
            log.info("当前未登陆，重定向登陆页面");
            response.sendRedirect("/login.html");
            return false;
        }
        log.info("在线的用户：【{}】", ChatEndPoint.onLineUsers.keySet());
        return true;
    }
}
