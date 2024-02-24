package com.su.service;

import com.su.domain.pojo.CompanyInformation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-02-24
 */
public interface CompanyInformationService extends IService<CompanyInformation> {

    /**
     * 修改公司信息
     * @param companyInformation
     * @return
     */
    Boolean updateCompanyInfo(CompanyInformation companyInformation);
}
