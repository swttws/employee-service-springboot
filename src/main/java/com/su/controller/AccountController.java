package com.su.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.common.response.ResultResponse;
import com.su.domain.pojo.Account;
import com.su.domain.vo.EmailVO;
import com.su.domain.vo.LoginVO;
import com.su.domain.vo.RegisterVO;
import com.su.service.AccountService;
import com.su.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin
public class AccountController {

    @Autowired
    private AccountService accountService;

    @ApiOperation("登录接口")
    @PostMapping("login")
    public ResultResponse login(@RequestBody LoginVO loginVO) {
        return ResultResponse.success(accountService.login(loginVO));
    }

    @ApiOperation("注册接口")
    @PostMapping("register")
    public ResultResponse register(@RequestBody RegisterVO registerVO) {
        return ResultResponse.success(accountService.register(registerVO));
    }

    @ApiOperation("发送邮箱验证码")
    @PostMapping("sendEmsCode")
    public ResultResponse sendEmsCode(@RequestBody EmailVO emailVO) {
        return ResultResponse.success(accountService.sendEmsCode(emailVO));
    }

    @ApiOperation("解析token")
    @GetMapping("/token/{token}")
    public ResultResponse token(@PathVariable("token") String token) {
        String userName = JwtUtils.getUserName(token);
        Account account = accountService.getOne(Wrappers.<Account>lambdaQuery().eq(Account::getUsername, userName));
        return ResultResponse.success(account);
    }

}

