package com.su.service;

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

}
