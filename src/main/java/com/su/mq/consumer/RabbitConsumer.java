package com.su.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.su.config.RabbitMQConfig;
import com.su.domain.es.ArticleEsEntity;
import com.su.domain.es.annoation.EsIndex;
import com.su.domain.pojo.Article;
import com.su.mapper.AccountMapper;
import com.su.mq.producer.RabbitProducer;
import com.su.mq.strategy.StrategyInit;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author suweitao
 */
@Component
@Slf4j
public class RabbitConsumer {

    /**
     * 监听es写入消费者
     */
    @RabbitListener(queues = RabbitMQConfig.TOPIC_QUEUE_NAME_ES)
    public void handEsMessage(String message) {
        RabbitProducer.MyMsg myMsg = JSONObject.parseObject(message, RabbitProducer.MyMsg.class);

        StrategyInit.strategyMap.get(myMsg.getType()).consumer(myMsg.getObject());
    }

}
