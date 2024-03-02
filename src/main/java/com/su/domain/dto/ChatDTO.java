package com.su.domain.dto;

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
public class ChatDTO {

    /**
     * 好友名称
     */
    private String name;

    /**
     * 未读消息数
     */
    private Integer count;

    /**
     * 聊天时间
     */
    private Date time;

    /**
     * id
     */
    private Integer id;
}
