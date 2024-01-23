package com.su.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.su.common.exception.MyException;
import com.su.domain.es.ArticleEsEntity;
import com.su.domain.pojo.Account;
import com.su.domain.pojo.Article;
import com.su.domain.vo.ArticleVO;
import com.su.domain.vo.SearchVO;
import com.su.mapper.ArticleMapper;
import com.su.mq.producer.RabbitProducer;
import com.su.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2023-12-26
 */
@Service
@Slf4j
public class ArticleServiceImp extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private RabbitProducer rabbitProducer;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 图片上传
     *
     * @return
     */
    @Override
    public byte[] upload(MultipartFile file) {
        byte[] bytes = null;
        try {
            bytes = file.getBytes();
        } catch (IOException ioException) {
            log.info("图片获取二进制流失败");
        }
        return bytes;
    }

    @Override
    public Integer saveOrUpdateArticle(ArticleVO articleVO) {
        if (StringUtils.isEmpty(articleVO.getContent()) || StringUtils.isEmpty(articleVO.getTitle())) {
            throw new MyException(500, "标题/内容不能为空");
        }
        Article article = baseMapper.selectById(articleVO.getId());
        //修改操作
        if (ObjectUtils.isNotEmpty(article)) {
            article.setContent(articleVO.getContent());
            article.setTitle(articleVO.getTitle());
            baseMapper.updateById(article);
        } else {
            article = new Article();
            article.setIsDeleted(true);
            article.setContent(articleVO.getContent());
            article.setTitle(articleVO.getTitle());
            Account account = ThreadLocalUtil.getAccount();
            article.setUserId(String.valueOf(account.getId()));
            baseMapper.insert(article);
        }
        return article.getId();
    }

    /**
     * 文章发布或修改
     *
     * @param articleVO
     */
    @Override
    public void sendArticle(ArticleVO articleVO) {
        if (ObjectUtils.isEmpty(articleVO.getGroupName()) || ObjectUtils.isEmpty(articleVO.getType())) {
            throw new MyException(500, "文章类型、时间或权限不能为空");
        }
        //查询文章
        Article article = baseMapper.selectById(articleVO.getId());
        if (Objects.isNull(article)) {
            throw new MyException(500, "系统异常,请联系管理员");
        }
        article.setGroupId(articleVO.getGroupId());
        article.setGroupName(articleVO.getGroupName());
        article.setSendTime(articleVO.getSendTime());
        article.setType(articleVO.getType());
        //设置为发布状态
        article.setIsDeleted(false);
        baseMapper.updateById(article);
        //通知mq，将文章数据写入es
        rabbitProducer.send(article);
    }

    /**
     * 首页文章显示
     *
     * @return
     */
    @Override
    public SearchVO search() {
        SearchVO searchVO = new SearchVO();
        try {
            //查询文章数据
            SearchRequest searchRequest = new SearchRequest("article_es_entity");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.sort(SortBuilders.fieldSort("viewNum").order(SortOrder.DESC));
            searchRequest.source(searchSourceBuilder);
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //处理返回结果
            SearchHit[] hits = response.getHits().getHits();
            List<SearchHit> searchHits = Arrays.asList(hits);
            ObjectMapper objectMapper = new ObjectMapper();
            List<ArticleEsEntity> articleEsEntityList = searchHits.stream().map(hit -> {
                //json字符串转换为实体类
                try {
                    return objectMapper.readValue(hit.getSourceAsString(), ArticleEsEntity.class);
                } catch (JsonProcessingException e) {
                    log.info("es返回结果转换实体类错误");
                    e.printStackTrace();
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            searchVO.setArticleEsEntityList(articleEsEntityList);

            //查询分类数据
            searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.aggregation(AggregationBuilders.terms("group_agg")
                    .field("groupName"));
            searchSourceBuilder.size(0);
            searchRequest.source(searchSourceBuilder);
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //获取分类数据，map
            Terms group_agg = response.getAggregations().get("group_agg");
            List<? extends Terms.Bucket> buckets = group_agg.getBuckets();
            List<String> groupNameList = buckets.stream()
                    .map(MultiBucketsAggregation.Bucket::getKeyAsString)
                    .collect(Collectors.toList());
            //集合转换为hashmap，key:name,value:groupName
            List<Map<String, String>> result = new ArrayList<>();
            groupNameList.forEach(groupName -> {
                Map<String, String> map = new HashMap<>();
                map.put("name", groupName);
                result.add(map);
            });
            searchVO.setGroupNameList(result);
        } catch (IOException ioException) {
            log.info("查询es文章数据异常");
            throw new MyException(500, "查询数据异常，请联系管理员");
        }

        return searchVO;
    }
}



















