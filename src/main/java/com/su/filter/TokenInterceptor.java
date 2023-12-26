package com.su.filter;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.common.exception.MyException;
import com.su.constant.CommonConstant;
import com.su.mapper.AccountMapper;
import com.su.pojo.Account;
import com.su.utils.JwtUtils;
import com.su.utils.ThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author suweitao
 * 用户信息拦截器
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 获取用户信息，存入ThreadLocal
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //放行登录、注册、发送邮箱验证码请求
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/account/login") || requestURI.equals("/account/register")
                || requestURI.equals("/account/sendEmsCode")) {
            return true;
        }
        String token = request.getHeader(CommonConstant.TOKEN);
        if (StringUtils.isEmpty(token)) {
            throw new MyException(500, "请登录");
        }
        String userName = JwtUtils.getUserName(token);
        Account account = accountMapper.selectOne(Wrappers.<Account>lambdaQuery().eq(Account::getUsername, userName));
        boolean verify = JwtUtils.verify(token, account);
        if (!verify) {
            throw new MyException(500, "请登录");
        }
        //保存为全局变量
        ThreadLocalUtil.setAccountThreadLocal(account);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.clear();
    }
}
