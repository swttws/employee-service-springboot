package com.su.mq.strategy;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.su.domain.es.ArticleEsEntity;
import com.su.domain.pojo.Article;
import com.su.mapper.AccountMapper;
import com.su.mq.strategy.annotation.EsUpdate;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author suweitao
 */
@Component
@Slf4j
@EsUpdate("Article")
public class ArticleEsUpdateConsumer implements EsUpdateConsumer{

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public void consumer(String message) {
        log.info("-------------文章ES消费者开始消费------------------");
        Article article = JSONObject.parseObject(message,Article.class);
        ArticleEsEntity articleEsEntity = new ArticleEsEntity();
        BeanUtils.copyProperties(article, articleEsEntity);
        articleEsEntity.setCollectNum(ObjectUtils.isEmpty(article.getCollectNum()) ? 0 : article.getCollectNum());
        articleEsEntity.setPraiseNum(ObjectUtils.isEmpty(article.getPraiseNum()) ? 0 : article.getPraiseNum());
        articleEsEntity.setViewNum(ObjectUtils.isEmpty(article.getViewNum()) ? 0 : article.getViewNum());
        articleEsEntity.setCommentNum(ObjectUtils.isEmpty(article.getCommentNum()) ? 0 : article.getCommentNum());
        articleEsEntity.setUserName(accountMapper.selectById(article.getUserId()).getUsername());
        articleEsEntity.setCreateTime(article.getCreateTime());
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
