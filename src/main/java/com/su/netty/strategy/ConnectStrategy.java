package com.su.netty.strategy;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.domain.pojo.Account;
import com.su.mapper.AccountMapper;
import com.su.netty.protocol.MyMessage;
import com.su.netty.protocol.Type;
import com.su.utils.JwtUtils;
import com.su.utils.ThreadLocalUtil;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//客户端请求连接处理事件
@Component
@TypeAnnotation(Type.CONNECT_EVENT)
@Slf4j
public class ConnectStrategy implements MessageStrategy {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    public static ConcurrentHashMap<Integer, Channel> userId2ChannelMap = new ConcurrentHashMap<>();

    //连接处理逻辑
    @Override
    public void handleMessage(Channel channel, MyMessage msg) {
        log.info("connect连接事件触发----");
        //解析消息体
        channel.attr(HandlerMessage.MY_MESSAGE_ATTRIBUTE_KEY).getAndSet(msg);
        //添加到在线用户列表
        HandlerMessage.onlineGroup.remove(channel);
        HandlerMessage.onlineGroup.add(channel);

        //保存用户id ：channel映射关系
        userId2ChannelMap.remove(msg.getSendUserId());
        userId2ChannelMap.put(msg.getSendUserId(), channel);

        log.info("映射关系:{}", userId2ChannelMap);
        log.info("新的连接建立：{}", channel);
        log.info("连接数" + HandlerMessage.onlineGroup.size());

    }

    //处理mq消息
    @Override
    public void dealMqMessage(MyMessage message) {

    }


}
