package com.luna.chatroom.controller;

import com.luna.chatroom.constant.UserConstant;
import com.luna.common.dto.ResultDTO;
import com.luna.common.dto.ResultDTOUtils;
import com.luna.common.dto.constant.ResultCode;
import com.luna.chatroom.ws.ChatEndPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * @author luna
 */
@RestController
@Slf4j
public class UserController {

    /**
     * 默认密码
     */
    private static final String PASSWORD = "123";

    @PostMapping("/login")
    public ResultDTO<String> toLogin(String username, String password, HttpSession httpSession) {
        // 7天有效
        httpSession.setMaxInactiveInterval(604800);

        if (PASSWORD.equals(password)) {
            log.info(username + "登录验证成功");
            httpSession.setAttribute(UserConstant.SESSION_KEY, username);
            return ResultDTOUtils.success(username);
        }

        log.info(username + "验证失败");
        return ResultDTOUtils.failure(ResultCode.PARAMETER_INVALID, "登陆失败，密码错误", null);
    }

    @GetMapping("/getUsername")
    public ResultDTO<String> getUsername(HttpSession httpSession) {
        String attribute = (String) httpSession.getAttribute(UserConstant.SESSION_KEY);
        return ResultDTOUtils.success(attribute);
    }

    @GetMapping("/getUsers")
    public ResultDTO<List<String>> getUsers(HttpSession httpSession) {
        String attribute = (String) httpSession.getAttribute(UserConstant.SESSION_KEY);
        return ResultDTOUtils.success(ChatEndPoint.getNamesExcludeMe(attribute));
    }

    @GetMapping("/logout")
    public ResultDTO<Void> logout(HttpSession httpSession) {
        String attribute = (String) httpSession.getAttribute(UserConstant.SESSION_KEY);
        ChatEndPoint.onLineUsers.remove(attribute);
        httpSession.removeAttribute(UserConstant.SESSION_KEY);
        return ResultDTOUtils.success();
    }

    @GetMapping("/offline")
    public ResultDTO<Void> offline(HttpSession httpSession) {
        String attribute = (String) httpSession.getAttribute(UserConstant.SESSION_KEY);
        ChatEndPoint chatEndPoint = ChatEndPoint.onLineUsers.get(attribute);
        try {
            chatEndPoint.getSession().close();
        } catch (IOException e) {
            return ResultDTOUtils.failure();
        }
        return ResultDTOUtils.success();
    }
}
