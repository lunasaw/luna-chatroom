package com.luna.chatroom.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author luna
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultMessage {
    private boolean isSystem;
    private String fromName;
    private List<String> toName;
    private Object message;
    private Date createTime;
}
