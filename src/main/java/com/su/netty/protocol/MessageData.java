package com.su.netty.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author suweitao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageData {

    /**
     * 发送消息者
     */
    private Integer sendUserId;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 消息
     */
    private Object data;

    /**
     * 发送者id
     */
    private String sendUserName;
}
