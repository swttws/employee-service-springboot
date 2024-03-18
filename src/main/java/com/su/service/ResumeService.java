package com.su.service;

import com.su.domain.dto.ResumeDTO;
import com.su.domain.pojo.Resume;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-03-18
 */
public interface ResumeService extends IService<Resume> {

    /**
     * 投递简历
     * @param resume
     * @return
     */
    Boolean deliver(Resume resume);

    /**
     * 查询简历列表
     * @return
     */
    List<ResumeDTO> getResume();


}
