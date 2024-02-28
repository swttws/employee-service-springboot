package com.su.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.su.common.exception.MyException;
import com.su.domain.dto.JobDetailDTO;
import com.su.domain.es.JobEsEntity;
import com.su.domain.pojo.Account;
import com.su.domain.pojo.CompanyInformation;
import com.su.domain.pojo.Job;
import com.su.domain.vo.QueryJobVO;
import com.su.mapper.JobMapper;
import com.su.mq.producer.RabbitProducer;
import com.su.service.AccountService;
import com.su.service.CompanyInformationService;
import com.su.service.JobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-02-24
 */
@Service
@Slf4j
public class JobServiceImp extends ServiceImpl<JobMapper, Job> implements JobService {

    @Autowired
    private CompanyInformationService companyInformationService;

    @Autowired
    private RabbitProducer rabbitProducer;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private AccountService accountService;

    /**
     * 保存或更新职位
     *
     * @param job
     * @return
     */
    @Override
    public Boolean saveOrUpdateJob(Job job) {
        if (ObjectUtils.isEmpty(job.getJobName()) || ObjectUtils.isEmpty(job.getWelfare())
                || ObjectUtils.isEmpty(job.getJobProperties()) || ObjectUtils.isEmpty(job.getMinSalary())
                || ObjectUtils.isEmpty(job.getMaxSalary()) || ObjectUtils.isEmpty(job.getType())) {
            throw new MyException(500, "请完善职位信息");
        }
        if (ObjectUtils.isNotEmpty(job.getId())) {
            if (job.getIsSend()==1){
                throw new MyException(500,"职位发布中，下架才可以修改");
            }
            baseMapper.deleteById(job);
        }
        Account account = ThreadLocalUtil.getAccount();
        CompanyInformation companyInformation = companyInformationService.getById(account.getThirdId());
        //判断公司信息是否完善
        if (ObjectUtils.isEmpty(companyInformation.getAddress())
                || ObjectUtils.isEmpty(companyInformation.getCompanyName())) {
            throw new MyException(500, "请先完善公司基本信息");
        }
        //设置基础信息
        job.setCreateTime(new Date());
        job.setCompanyId(String.valueOf(companyInformation.getId()));
        if(ObjectUtils.isEmpty(job.getAddress())){
            job.setAddress(companyInformation.getAddress());
        }
        //设置为未发布
        job.setIsSend(2);
        baseMapper.insert(job);
        return true;
    }

    /**
     * 获取职位列表
     *
     * @param current
     * @return
     */
    @Override
    public Page<Job> listJob(Integer current) {
        Page<Job> page = new Page<>();
        page.setCurrent(current);
        page.setSize(7);
        return baseMapper.selectPage(page, null);
    }

    /**
     * 删除职位
     *
     * @param jobId
     * @return
     */
    @Override
    public Boolean deleteJob(Integer jobId) {
        Job job = baseMapper.selectById(jobId);
        if (job == null) {
            throw new MyException(500, "职位不存在");
        }
        if (job.getIsSend() == 1) {
            throw new MyException(500, "职位发布中，请先下架再删除");
        }
        baseMapper.deleteById(job);
        return true;
    }

    /**
     * 职位撤销与发布
     * @param jobId
     * @param type 1发布 2未发布
     * @return
     */
    @Override
    public Boolean operatorJob(Integer jobId, Integer type) {
        Job job = baseMapper.selectById(jobId);
        job.setIsSend(type);
        //TODO 审核机制
        baseMapper.updateById(job);
        //职位发布，更新es
        rabbitProducer.send(job,"Job");
        return true;
    }

    /**
     * 带条件查询职位列表
     * @param queryJobVO
     * @return
     */
    @Override
    public List<JobEsEntity> queryList(QueryJobVO queryJobVO) {
        SearchRequest searchRequest = new SearchRequest("job_es_entity");

        //构造条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //已发布
        boolQueryBuilder.must().add(QueryBuilders.termQuery("isSend",1));
        if(StringUtils.isNotEmpty(queryJobVO.getType())){
            boolQueryBuilder.must().add(QueryBuilders.matchQuery("type",queryJobVO.getType()));
        }
        if(StringUtils.isNotEmpty(queryJobVO.getJobProperties())){
            boolQueryBuilder.must().add(QueryBuilders.matchQuery("jobProperties",queryJobVO.getJobProperties()));
        }
        if(StringUtils.isNotEmpty(queryJobVO.getCondition())){
            boolQueryBuilder.must().add(QueryBuilders.matchQuery("jobName",queryJobVO.getCondition()));
        }
        if(StringUtils.isNotEmpty(queryJobVO.getSalaryRange())){
            String[] split = queryJobVO.getSalaryRange().split("-");
            Integer minSalary = Integer.valueOf(split[0]);
            Integer maxSalary = Integer.valueOf(split[1]);
            boolQueryBuilder.must().add(QueryBuilders.rangeQuery("minSalary").gte(minSalary));
            boolQueryBuilder.must().add(QueryBuilders.rangeQuery("maxSalary").lte(maxSalary));
        }

        List<JobEsEntity> jobEsEntityList=new ArrayList<>();
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            for (SearchHit hit : hits) {
                String sourceAsString = hit.getSourceAsString();
                JobEsEntity jobEsEntity = JSONObject.parseObject(sourceAsString, JobEsEntity.class);
                jobEsEntityList.add(jobEsEntity);
            }
        } catch (IOException ioException) {
            log.info("es查询职位数据出错");
            ioException.printStackTrace();
        }
        return jobEsEntityList;
    }

    /**
     * 获取职详情和相关信息
     * @param jobId
     * @return
     */
    @Override
    public JobDetailDTO getJobDetail(Integer jobId) {
        Job job = baseMapper.selectById(jobId);
        CompanyInformation companyInformation = companyInformationService.getById(job.getCompanyId());
        Account account = accountService.getOne(Wrappers.<Account>lambdaQuery()
                .eq(Account::getThirdId, companyInformation.getId()));
        JobDetailDTO jobDetailDTO = new JobDetailDTO();
        jobDetailDTO.setJob(job);
        jobDetailDTO.setCompanyInformation(companyInformation);
        jobDetailDTO.setAccount(account);
        return jobDetailDTO;
    }
}
