package com.luna.chatroom.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luna.chatroom.pojo.ResultMessage;

import java.util.Date;
import java.util.List;


/**
 * @author luna
 */
public class MessageUtils {

    public static String getMessage(boolean isSystemMessage, String fromName, List<String> toNames, Object message) {
        return JSON.toJSONString(new ResultMessage(isSystemMessage, fromName, toNames, message, new Date()));
    }

    public static String getMessage(boolean isSystemMessage, String fromName, Object message) {
        try {
            ResultMessage resultMessage = new ResultMessage();
            resultMessage.setSystem(isSystemMessage);
            resultMessage.setMessage(message);
            if (fromName != null) {
                resultMessage.setFromName(fromName);
            }
//            if(toName !=null ){
//                resultMessage.setToName(toName);
//            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(resultMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
