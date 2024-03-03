package com.su.service;

import com.su.domain.dto.ChatDTO;
import com.su.netty.protocol.MessageData;
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

    void saveRedis(String friendListKey, Integer otherId,Integer isAdd);

    /**
     * 获取消息
     * @param myId
     * @param otherId
     * @return
     */
    List<MessageData> getChatList(Integer myId, Integer otherId);

    /**
     * 清除未读消息
     * @param myId
     * @param otherId
     * @return
     */
    Boolean removeUnRead(Integer myId, Integer otherId);
}
