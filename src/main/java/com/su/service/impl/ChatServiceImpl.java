package com.su.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.su.constant.CommonConstant;
import com.su.constant.RedisConstant;
import com.su.domain.dto.ChatDTO;
import com.su.domain.pojo.Account;
import com.su.netty.protocol.MyMessage;
import com.su.service.AccountService;
import com.su.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author suweitao
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AccountService accountService;

    /**
     * 保存聊天列表
     *
     * @param myId
     * @param otherId
     * @return
     */
    @Override
    public Boolean saveFriendList(Integer myId, Integer otherId) {
        //保存我这边聊天列表
        String friendListKey = RedisConstant.getFriendListKey(myId);
        saveRedis(friendListKey, otherId);
        //保存对方聊天列表
        friendListKey = RedisConstant.getFriendListKey(otherId);
        saveRedis(friendListKey, myId);
        return true;
    }

    /**
     * 保存聊天列表
     *
     * @param friendListKey
     * @param otherId
     */
    private void saveRedis(String friendListKey, Integer otherId) {
        int unReadCount = 0;
        Set<Object> unReadMsgSet = redisTemplate.boundZSetOps(friendListKey).range(0, -1);
        Long time = 0L;

        if (ObjectUtils.isNotEmpty(unReadMsgSet)) {
            //获取未读消息
            List<Integer> collect = unReadMsgSet.stream().map(value -> (String) value)
                    .filter(value -> otherId.equals(Integer.valueOf(value.split(CommonConstant.REDIS_SEPARATORS)[0])))
                    .map(value -> {
                        // 未读消息数
                        return Integer.valueOf(value.split(CommonConstant.REDIS_SEPARATORS)[1]);
                    }).collect(Collectors.toList());
            unReadCount = ObjectUtils.isNotEmpty(collect) ? collect.get(0) : 0;
            //获取时间
            List<Long> timeList = unReadMsgSet.stream().map(value -> (String) value)
                    .filter(value -> otherId.equals(Integer.valueOf(value.split(CommonConstant.REDIS_SEPARATORS)[0])))
                    .map(value -> {
                        // 未读消息数
                        return Long.valueOf(value.split(CommonConstant.REDIS_SEPARATORS)[2]);
                    }).collect(Collectors.toList());
            time = ObjectUtils.isNotEmpty(timeList) ? timeList.get(0) : 0L;
        }
        // 用户在列表中，则删除对应记录
        redisTemplate.opsForZSet().remove(friendListKey,
                RedisConstant.getOtherIdMsg(otherId, unReadCount, time));
        //添加用户列表记录
        redisTemplate.opsForZSet().add(friendListKey,
                RedisConstant.getOtherIdMsg(otherId, unReadCount, System.currentTimeMillis()), System.currentTimeMillis());
    }

    /**
     * 获取好友列表
     *
     * @param id
     * @return
     */
    @Override
    public List<ChatDTO> getFriendList(Integer id) {
        List<ChatDTO> result = new ArrayList<>();
        String friendListKey = RedisConstant.getFriendListKey(id);
        Set<Object> friendList = redisTemplate.opsForZSet().range(friendListKey, 0, -1);
        //处理好友信息
        if (ObjectUtils.isNotEmpty(friendList)) {
            friendList.stream().forEach(value -> {
                // 好友id
                Integer friendId = Integer.valueOf(((String) value).split(CommonConstant.REDIS_SEPARATORS)[0]);
                // 未读消息数
                Integer count = Integer.valueOf(((String) value).split(CommonConstant.REDIS_SEPARATORS)[1]);
                Account account = accountService.getById(friendId);
                // 获取聊天时间
                long score = Long.parseLong(((String) value).split(CommonConstant.REDIS_SEPARATORS)[2]);
                ChatDTO chatDTO = new ChatDTO(account.getUsername(), count, new Date(score), friendId);
                result.add(chatDTO);
            });
        }
        return result;
    }

    /**
     * 保存聊天记录
     *
     * @param myMessage
     * @return
     */
    @Override
    public Boolean saveChat(MyMessage myMessage) {
        // 获取发送人信息
        Account sendUser = accountService.getById(myMessage.getSendUserId());
        //获取接收人信息
        Account receiverUser = accountService.getById(myMessage.getReceiverId());
        String msg = (String) myMessage.getData();

        //构造消息key，value,保存聊天记录
        String chatKey = RedisConstant.getChatKey(sendUser.getId(), receiverUser.getId());
        String chatValue = RedisConstant.getChatValue(sendUser.getUsername(), msg);
        redisTemplate.opsForList().leftPush(chatKey, chatValue);
        return true;
    }
}
