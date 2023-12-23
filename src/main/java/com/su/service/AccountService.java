package com.su.service;

import com.su.domain.vo.EmailVO;
import com.su.domain.vo.LoginVO;
import com.su.domain.vo.RegisterVO;
import com.su.pojo.Account;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2023-12-19
 */
public interface AccountService extends IService<Account> {

    /**
     * 发送邮箱验证码
     * @param emailVO
     * @return
     */
    Boolean sendEmsCode(EmailVO emailVO);

    /**
     * 注册接口
     * @param registerVO
     * @return
     */
    Boolean register(RegisterVO registerVO);

    /**
     * 登录接口
     * @param loginVO
     * @return 返回token
     */
    String login(LoginVO loginVO);
}
