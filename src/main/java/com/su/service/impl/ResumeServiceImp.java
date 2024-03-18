package com.su.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.domain.dto.ResumeDTO;
import com.su.domain.pojo.Job;
import com.su.domain.pojo.Resume;
import com.su.domain.pojo.UserInformation;
import com.su.mapper.CompanyInformationMapper;
import com.su.mapper.JobMapper;
import com.su.mapper.ResumeMapper;
import com.su.mapper.UserInformationMapper;
import com.su.service.ResumeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.utils.ThreadLocalUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-03-18
 */
@Service
public class ResumeServiceImp extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {

    @Autowired
    private UserInformationMapper userInformationMapper;

    @Autowired
    private JobMapper jobMapper;

    /**
     * 投递简历
     *
     * @param resume
     * @return
     */
    public Boolean deliver(Resume resume) {
        //删除以往投递记录
        Integer userId = ThreadLocalUtil.getAccount().getId();
        resume.setUserId(userId);
        resume.setCreateTime(new Date());
        baseMapper.delete(Wrappers.<Resume>lambdaQuery()
                .eq(Resume::getUserId, userId)
                .eq(Resume::getCompanyId, resume.getCompanyId())
                .eq(Resume::getJobId, resume.getJobId()));
        resume.setStatus(0);
        baseMapper.insert(resume);
        return true;
    }

    /**
     * 查询简历列表
     *
     * @return
     */
    @Override
    public List<ResumeDTO> getResume() {
        Integer companyId = ThreadLocalUtil.getAccount().getThirdId();
        List<Resume> resumeList = baseMapper.selectList(Wrappers.<Resume>lambdaQuery()
                .eq(Resume::getCompanyId, companyId));
        //处理数据
        List<ResumeDTO> result = new ArrayList<>();
        resumeList.forEach(resume -> {
            ResumeDTO resumeDTO = new ResumeDTO();
            BeanUtils.copyProperties(resume, resumeDTO);
            resumeDTO.setId(resume.getId());
            //获取用户信息
            UserInformation userInformation = userInformationMapper.selectOne(
                    Wrappers.<UserInformation>lambdaQuery()
                            .eq(UserInformation::getAccountId, resume.getUserId())
            );
            resumeDTO.setName(userInformation.getName());
            resumeDTO.setSchool(userInformation.getSchool());
            resumeDTO.setTime(resume.getCreateTime());
            //获取职位信息
            Job job = jobMapper.selectById(resume.getJobId());
            resumeDTO.setJobName(job.getJobName());

            result.add(resumeDTO);
        });
        return result;
    }
}
