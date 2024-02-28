package com.su.domain.dto;

import com.su.domain.pojo.Account;
import com.su.domain.pojo.CompanyInformation;
import com.su.domain.pojo.Job;
import lombok.Data;

/**
 * @author suweitao
 */
@Data
public class JobDetailDTO {

    private Job job;

    private Account account;

    private CompanyInformation companyInformation;

}
