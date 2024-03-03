package com.su.netty.strategy;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.su.constant.RedisConstant;
import com.su.mapper.AccountMapper;
import com.su.netty.protocol.MessageData;
import com.su.netty.protocol.MyMessage;
import com.su.netty.protocol.Type;
import com.su.service.ChatService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@TypeAnnotation(Type.CHAT_ONLY)
@Component
public class ChatOnlyStrategy implements MessageStrategy {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private ChatService chatService;


    //处理单聊消息
    @Override
    public void handleMessage(Channel channel, MyMessage msg) {
        //判断消息接收者用户是否在线
        List<MyMessage> myMessageList = HandlerMessage.onlineGroup.stream().filter(onlineChannel -> {
            MyMessage myMessage = onlineChannel.attr(HandlerMessage.MY_MESSAGE_ATTRIBUTE_KEY).get();
            //不能使用equal
            if (myMessage.getSendUserId() == msg.getReceiverId()) {
                return true;
            } else {
                return false;
            }
        }).map(item -> item.attr(HandlerMessage.MY_MESSAGE_ATTRIBUTE_KEY).get())
                .collect(Collectors.toList());

        //构建redis key  用户id:企业id
        Integer type = accountMapper.selectById(msg.getSendUserId()).getType();
        String redisKey = type == 2 ? RedisConstant.getChatContentKey(msg.getReceiverId(), msg.getSendUserId())
                : RedisConstant.getChatContentKey(msg.getSendUserId(), msg.getReceiverId());
        //封装消息
        MessageData messageData = new MessageData();
        BeanUtils.copyProperties(msg, messageData);
        //聊天记录保存redis
        redisTemplate.opsForList().leftPush(redisKey, JSONObject.toJSONString(messageData));

        //修改对方聊天列表未读消息数
        if (myMessageList.size() <= 0 || myMessageList.get(0).getReceiverId()!=msg.getSendUserId()) {
            String friendListKey = RedisConstant.getFriendListKey(msg.getReceiverId());
            chatService.saveRedis(friendListKey, msg.getSendUserId(), 1);
        }

        //写消息给对方客户端
        HandlerMessage.onlineGroup.stream().filter(onlineChannel ->
                onlineChannel.attr(HandlerMessage.MY_MESSAGE_ATTRIBUTE_KEY).get().
                        getSendUserId().equals(msg.getReceiverId())
        ).forEach(onlineChannel -> {
            onlineChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(messageData)));
        });
        //写给自己
        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(messageData)));
    }

    //处理mq消息消费逻辑
    @Override
    public void dealMqMessage(MyMessage message) {

    }
}
