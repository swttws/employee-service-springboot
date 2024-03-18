package com.su.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.common.response.ResultResponse;
import com.su.domain.pojo.UserInformation;
import com.su.service.UserInformationService;
import com.su.utils.ThreadLocalUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-03-12
 */
@RestController
@RequestMapping("/userInformation")
@CrossOrigin
public class UserInformationController {

    @Autowired
    private UserInformationService userInformationService;

    @ApiOperation("完善个人信息")
    @PostMapping("updateInfo")
    public ResultResponse updateInfo(@RequestBody UserInformation userInformation) {
        return ResultResponse.success(userInformationService.updateInfo(userInformation));
    }

    @ApiOperation("查询个人信息")
    @GetMapping("getInfo")
    public ResultResponse getInfo() {
        return ResultResponse.success(userInformationService.getOne(
                Wrappers.<UserInformation>lambdaQuery()
                        .eq(UserInformation::getAccountId, ThreadLocalUtil.getAccount().getId())
        ));
    }


}

