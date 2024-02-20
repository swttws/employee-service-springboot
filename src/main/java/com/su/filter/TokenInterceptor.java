package com.su.filter;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.common.exception.GlobalExceptionHandler;
import com.su.common.exception.MyException;
import com.su.common.response.ResultResponse;
import com.su.constant.CommonConstant;
import com.su.mapper.AccountMapper;
import com.su.domain.pojo.Account;
import com.su.utils.JwtUtils;
import com.su.utils.ThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author suweitao
 * 用户信息拦截器
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private AccountMapper accountMapper;

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    private final List<String> url = Arrays.asList("/account/login", "/account/register",
            "/account/sendEmsCode", "/article/upload","/article/search","/error","/article/filterWord");


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
        if (request.getMethod().equals("OPTIONS")) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
            response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS");
            response.setStatus(HttpStatus.OK.value());
            return false;
        }
        //放行登录、注册、发送邮箱验证码请求
        String requestURI = request.getRequestURI();
        if (url.contains(requestURI)) {
            return true;
        }
        String token = request.getHeader(CommonConstant.TOKEN);
        if (StringUtils.isEmpty(token)) {
            return handlerError(response);
        }
        String userName = JwtUtils.getUserName(token);
        Account account = accountMapper.selectOne(Wrappers.<Account>lambdaQuery().eq(Account::getUsername, userName));
        boolean verify = JwtUtils.verify(token, account);
        if (!verify) {
            return handlerError(response);
        }
        //保存为全局变量
        ThreadLocalUtil.setAccountThreadLocal(account);
        return true;
    }

    public boolean handlerError(HttpServletResponse response) throws IOException {
        ResultResponse resultResponse = globalExceptionHandler.myExceptionHandler(new MyException(500, "请登录"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.getWriter().write(JSONObject.toJSONString(resultResponse));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.clear();
    }
}
