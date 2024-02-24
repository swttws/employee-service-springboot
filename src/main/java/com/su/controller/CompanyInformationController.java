package com.su.controller;


import com.su.common.response.ResultResponse;
import com.su.domain.pojo.Account;
import com.su.domain.pojo.CompanyInformation;
import com.su.service.CompanyInformationService;
import com.su.utils.ThreadLocalUtil;
import io.swagger.annotations.ApiOperation;
import jodd.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-02-24
 */
@RestController
@RequestMapping("/companyInformation")
@CrossOrigin
public class CompanyInformationController {

    @Autowired
    private CompanyInformationService companyInformationService;

    @ApiOperation("获取公司信息")
    @GetMapping("getCompanyInfo")
    public ResultResponse getCompanyInfo(){
        Account account = ThreadLocalUtil.getAccount();
        CompanyInformation companyInformation = companyInformationService.getById(account.getThirdId());
        return ResultResponse.success(companyInformationService.getById(companyInformation.getId()));
    }

    @ApiOperation("修改公司信息")
    @PostMapping("updateCompanyInfo")
    public ResultResponse updateCompanyInfo(@RequestBody CompanyInformation companyInformation){
        return ResultResponse.success(companyInformationService.updateCompanyInfo(companyInformation));
    }

}

