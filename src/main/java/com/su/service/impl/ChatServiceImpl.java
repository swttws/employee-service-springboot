package com.su.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.su.constant.CommonConstant;
import com.su.constant.RedisConstant;
import com.su.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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

    /**
     * 保存聊天列表
     *
     * @param myId
     * @param otherId
     * @return
     */
    @Override
    public Boolean saveFriendList(Integer myId, Integer otherId) {
        String friendListKey = RedisConstant.getFriendListKey(myId);
        int unReadCount = 0;
        //获取未读消息数
        Set<Object> unReadMsgSet = redisTemplate.boundZSetOps(friendListKey).range(0, -1);
        if (ObjectUtils.isNotEmpty(unReadMsgSet)) {
            List<Integer> collect = unReadMsgSet.stream().map(value -> (String) value)
                    .filter(value -> otherId.equals(Integer.valueOf(value.split(CommonConstant.REDIS_SEPARATORS)[0])))
                    .map(value -> {
                        // 好友id
                        Integer key = Integer.valueOf(value.split(CommonConstant.REDIS_SEPARATORS)[0]);
                        // 未读消息数
                        return Integer.valueOf(value.split(CommonConstant.REDIS_SEPARATORS)[1]);
                    }).collect(Collectors.toList());
            unReadCount = ObjectUtils.isNotEmpty(collect) ? collect.get(0) : 0;
        }
        // 用户在列表中，则删除对应记录
        redisTemplate.opsForZSet().remove(friendListKey, RedisConstant.getOtherIdMsg(otherId, unReadCount));
        //添加用户列表记录
        redisTemplate.opsForZSet().add(friendListKey, RedisConstant.getOtherIdMsg(otherId,unReadCount), System.currentTimeMillis());
        return true;
    }
}
