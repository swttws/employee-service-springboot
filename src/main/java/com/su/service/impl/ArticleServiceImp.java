package com.su.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.su.common.exception.MyException;
import com.su.constant.RedisConstant;
import com.su.domain.es.ArticleEsEntity;
import com.su.domain.pojo.Account;
import com.su.domain.pojo.Article;
import com.su.domain.vo.ArticleVO;
import com.su.domain.vo.SearchVO;
import com.su.mapper.ArticleMapper;
import com.su.mq.producer.RabbitProducer;
import com.su.service.AccountService;
import com.su.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.utils.ThreadLocalUtil;
import com.su.utils.TrimTreeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
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

    private static final String REPLACE_WORD = "**";

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private AccountService accountService;

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
        rabbitProducer.send(article,"Article");
    }

    /**
     * 首页文章显示
     *
     * @return
     */
    @Override
    public SearchVO search(String query) {
        SearchVO searchVO = new SearchVO();
        try {
            //查询文章数据
            SearchRequest searchRequest = new SearchRequest("article_es_entity");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.sort(SortBuilders.fieldSort("viewNum").order(SortOrder.DESC));
            //有查询条件
            if (StringUtils.isNotEmpty(query)) {
                searchSourceBuilder.query(QueryBuilders.matchQuery("title", query));
            }

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
            //有查询条件
            if (StringUtils.isNotEmpty(query)) {
                searchSourceBuilder.query(QueryBuilders.matchQuery("title", query));
            }
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

    /**
     * 过滤敏感词
     *
     * @param word
     * @return
     */
    @Override
    public String filterWord(String word) {
        if (StringUtils.isBlank(word)) {
            return "";
        }
        //指针1指向字典树根节点
        TrimTreeUtil.Trim beginTrim = TrimTreeUtil.trim;
        //替换的字符串
        StringBuilder stringBuilder = new StringBuilder();
        //两个指针指向字符串区间
        int begin = 0;
        int end = 0;
        while (end < word.length()) {
            char c = word.charAt(end);
            //为标点符号
            if (TrimTreeUtil.isSymbol(c)) {
                //begin对应的字符不在字典树中
                if (beginTrim == TrimTreeUtil.trim) {
                    stringBuilder.append(c);//符号追加
                    begin++;
                }
                end++;
                continue;
            }
            //获取子节点
            beginTrim = beginTrim.getSubTrim(c);
            //字符不存在字典树中
            if (beginTrim == null) {
                //begin字符不是敏感词
                stringBuilder.append(word.charAt(begin));
                end = ++begin;
                beginTrim = TrimTreeUtil.trim;
            }
            //字符存在字典树中，且为结尾
            else if (beginTrim.getIsEnd()) {
                //采用*替代字符串
                stringBuilder.append(REPLACE_WORD);
                begin = ++end;
                //字典树回到根节点
                beginTrim = TrimTreeUtil.trim;
            }
            //字符在字典树
            else {
                //继续查找下一个字符
                end++;
            }
        }
        //添加剩余字符串
        stringBuilder.append(word.substring(begin));
        return stringBuilder.toString();
    }

    /**
     * 添加用户搜索记录
     *
     * @param word
     */
    @Override
    public Boolean addSearchWord(String word) {
        String username = ThreadLocalUtil.getAccount().getUsername();
        //保存用户搜索记录
        String redisKey = RedisConstant.getSearchWordKey(username);
        //若队列中有搜索过改数据，先删除再添加
        redisTemplate.opsForList().remove(redisKey,0,word);
        redisTemplate.opsForList().leftPush(redisKey, word);
        //保存热门搜索记录
        Double score = redisTemplate.opsForZSet().score(RedisConstant.HOT_SEARCH_KEY, word);
        if (score == null) {
            redisTemplate.opsForZSet().add(RedisConstant.HOT_SEARCH_KEY, word, 1);
        } else {
            redisTemplate.opsForZSet().incrementScore(RedisConstant.HOT_SEARCH_KEY,word,1);
        }
        return true;
    }

    /**
     * 删除搜索记录
     * @return
     */
    @Override
    public Boolean deleteSearchWord() {
        String username = ThreadLocalUtil.getAccount().getUsername();
        //保存用户搜索记录
        String redisKey = RedisConstant.getSearchWordKey(username);
        redisTemplate.delete(redisKey);
        return true;
    }

    /**
     * 展示历史搜索记录
     * type 1历史记录 2热门记录
     * @return
     */
    @Override
    public List<String> getSearchWord(Integer type) {
        String username = ThreadLocalUtil.getAccount().getUsername();
        //保存用户搜索记录
        String redisKey = RedisConstant.getSearchWordKey(username);
        if(type==1){
            List<Object> range = redisTemplate.opsForList().range(redisKey, 0, -1);
            if(ObjectUtils.isEmpty(range)){
                return new ArrayList<>();
            }else{
                return range.stream().map(o->(String)o).collect(Collectors.toList());
            }
        }else{
            Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().rangeWithScores(RedisConstant.HOT_SEARCH_KEY, 0, 9);
            if(ObjectUtils.isEmpty(typedTuples)){
                return new ArrayList<>();
            }else{
                return typedTuples.stream().map(t->(String)t.getValue()).collect(Collectors.toList());
            }
        }
    }

    /**
     * 访问文章详情
     * @param id
     * @return
     */
    @Override
    public Article getByIdAndAdd(Integer id) {
        Article article = baseMapper.selectById(id);
        article.setUsername(accountService.getById(article.getUserId()).getUsername());
        //更新es和数据库
        article.setViewNum(Objects.isNull(article.getViewNum())?0:article.getViewNum()+1);
        baseMapper.updateById(article);
        rabbitProducer.send(article,"Article");
        return article;
    }
}



















