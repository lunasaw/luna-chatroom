package com.luna.chatroom.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luna
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String toName;
    private String message;
    private String fromName;
}
