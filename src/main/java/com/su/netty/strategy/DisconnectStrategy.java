package com.su.netty.strategy;

import com.su.netty.protocol.MyMessage;
import com.su.netty.protocol.Type;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@TypeAnnotation(Type.DISCONNECT_EVENT)
@Slf4j
public class DisconnectStrategy implements MessageStrategy{

    //消息处理
    @Override
    public void handleMessage(Channel channel, MyMessage msg) {
        log.info("连接关闭事件----------------");
        ConnectStrategy.userId2ChannelMap.remove(msg.getSendUserId());
        HandlerMessage.onlineGroup.remove(channel);

        log.info("有连接关闭");
        log.info("连接数：{}",HandlerMessage.onlineGroup.size());
        log.info("连接对象：{}",ConnectStrategy.userId2ChannelMap);
    }

    @Override
    public void dealMqMessage(MyMessage message) {

    }
}
