package com.su.service;

import com.su.domain.dto.ChatDTO;
import com.su.netty.protocol.MyMessage;

import java.util.List;

/**
 * @author suweitao
 */
public interface ChatService {

    /**
     * 保存聊天列表
     * @param myId
     * @param otherId
     * @return
     */
    Boolean saveFriendList(Integer myId, Integer otherId);

    /**
     * 获取好友列表
     * @param id
     * @return
     */
    List<ChatDTO> getFriendList(Integer id);

    /**
     * 保存聊天记录
     * @param myMessage
     * @return
     */
    Boolean saveChat(MyMessage myMessage);
}
