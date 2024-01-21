package com.su.mq.producer;

import com.alibaba.fastjson.JSONObject;
import com.su.config.RabbitMQConfig;
import com.su.domain.pojo.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

/**
 * @author suweitao
 */
@Component
@Slf4j
public class RabbitProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(Article message){
        //发送消息
        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE_NAME,
                RabbitMQConfig.TOPIC_ROUTING_KEY_ES, JSONObject.toJSONString(message));
        log.info("---------生成者发送消息成功---------");
    }

}
