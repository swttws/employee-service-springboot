package com.su.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.su.domain.pojo.Job;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-02-24
 */
public interface JobService extends IService<Job> {

    /**
     * 保存职位
     * @param job
     * @return
     */
    Boolean saveOrUpdateJob(Job job);

    /**
     * 获取职位分页列表
     * @return
     */
    Page<Job> listJob(Integer current);

    /**\
     * 删除职位
     * @param jobId
     * @return
     */
    Boolean deleteJob(Integer jobId);

    /**
     * 职位发布与撤销
     * @param jobId
     * @param type
     * @return
     */
    Boolean operatorJob(Integer jobId, Integer type);
}
