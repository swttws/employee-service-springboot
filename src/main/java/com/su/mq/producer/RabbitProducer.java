package com.su.mq.producer;

import com.su.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author suweitao
 */
@Component
@Slf4j
public class RabbitProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(Object message,String tagId){
        CorrelationData correlationData = new CorrelationData(tagId);
        //发送确认机制
        rabbitTemplate.setConfirmCallback((flag, ack, cause) -> {
            if(ack){
                log.info("消息id为：{}，发送消息：{}成功",flag,message);
            }else{
                log.info("消息id为：{}，发送消息：{}失败",flag,message);
            }
        });
        //发送消息
        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE_NAME,
                RabbitMQConfig.TOPIC_ROUTING_KEY_ES,message,correlationData);
    }

}
