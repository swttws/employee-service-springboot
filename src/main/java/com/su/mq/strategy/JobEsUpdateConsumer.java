package com.su.mq.strategy;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.su.domain.es.ArticleEsEntity;
import com.su.domain.es.JobEsEntity;
import com.su.domain.pojo.Article;
import com.su.domain.pojo.Job;
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
@Slf4j
@EsUpdate("Job")
@Component
public class JobEsUpdateConsumer implements EsUpdateConsumer{

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public void consumer(String message) {
        log.info("-------------职位ES消费者开始消费------------------");
        Job job = JSONObject.parseObject(message, Job.class);
        JobEsEntity jobEsEntity = new JobEsEntity();
        BeanUtils.copyProperties(job, jobEsEntity);
        jobEsEntity.setCreateTime(job.getCreateTime());
        //文章数据写入es
        try {
            UpdateRequest updateRequest = new UpdateRequest("job_es_entity", String.valueOf(job.getId()))
                    .doc(entityToJSON(jobEsEntity), XContentType.JSON)
                    //文档不存在则插入
                    .docAsUpsert(true);
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            log.info("---------ES消费者消费成功--------");
        } catch (Exception e) {
            log.info("------------ES消费者消息消息异常-------------");
            e.printStackTrace();
        }
    }

    private String entityToJSON(JobEsEntity entity) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(entity);
    }
}
