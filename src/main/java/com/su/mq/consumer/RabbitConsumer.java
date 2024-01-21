package com.su.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.su.config.RabbitMQConfig;
import com.su.domain.es.ArticleEsEntity;
import com.su.domain.es.annoation.EsIndex;
import com.su.domain.pojo.Article;
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

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 监听es写入消费者
     */
    @RabbitListener(queues = RabbitMQConfig.TOPIC_QUEUE_NAME_ES)
    public void handEsMessage(String message){
        log.info("-------------文章ES消费者开始消费------------------");
        Article article = JSONObject.parseObject(message, Article.class);
        ArticleEsEntity articleEsEntity = new ArticleEsEntity();
        BeanUtils.copyProperties(article,articleEsEntity);
        articleEsEntity.setCollectNum(0);
        articleEsEntity.setPraiseNum(0);
        articleEsEntity.setViewNum(0);
        articleEsEntity.setCommentNum(0);
        //文章数据写入es
        try {
            UpdateRequest updateRequest = new UpdateRequest("article_es_entity", String.valueOf(article.getId()))
                    .doc(entityToJSON(articleEsEntity), XContentType.JSON)
                    //文档不存在则插入
                    .docAsUpsert(true);
           restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
           log.info("---------ES消费者消费成功--------");
        } catch (Exception e) {
            log.info("------------ES消费者消息消息异常-------------");
            e.printStackTrace();
        }
    }

    private String entityToJSON(ArticleEsEntity entity) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(entity);
    }

}
