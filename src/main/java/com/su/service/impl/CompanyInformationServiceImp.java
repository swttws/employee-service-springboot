package com.su.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.common.exception.MyException;
import com.su.domain.pojo.CompanyInformation;
import com.su.mapper.CompanyInformationMapper;
import com.su.service.CompanyInformationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-02-24
 */
@Service
public class CompanyInformationServiceImp extends ServiceImpl<CompanyInformationMapper, CompanyInformation> implements CompanyInformationService {

    /**
     * 修改公司信息
     *
     * @param companyInformation
     * @return
     */
    @Override
    public Boolean updateCompanyInfo(CompanyInformation companyInformation) {
        if (StringUtils.isEmpty(companyInformation.getCompanyName())) {
            throw new MyException(500, "公司名不能为空");
        }
        Long count = baseMapper.selectCount(Wrappers.<CompanyInformation>lambdaQuery()
                .eq(CompanyInformation::getCompanyName, companyInformation.getCompanyName())
                .ne(CompanyInformation::getId, companyInformation.getId()));
        if (count > 0) {
            throw new MyException(500, "公司名已经存在");
        }
        baseMapper.updateById(companyInformation);
        return true;
    }
}
