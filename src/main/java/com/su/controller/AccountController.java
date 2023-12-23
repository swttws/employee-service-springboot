package com.su.controller;


import com.su.common.response.ResultResponse;
import com.su.domain.vo.EmailVO;
import com.su.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2023-12-19
 */
@RestController
@RequestMapping("/account")
@Api("注册登录contoller")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @ApiOperation("登录接口")
    @PostMapping("login")
    public ResultResponse login() {
        return null;
    }

    @ApiOperation("注册接口")
    @PostMapping("register")
    public ResultResponse register() {
        return null;
    }

    @ApiOperation("发送邮箱验证码")
    @PostMapping("sendEmsCode")
    public ResultResponse sendEmsCode(@RequestBody EmailVO emailVO) {
        return ResultResponse.success(accountService.sendEmsCode(emailVO));
    }
}

