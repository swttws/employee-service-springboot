package com.su.service.impl;

import com.su.common.exception.MyException;
import com.su.constant.RedisConstant;
import com.su.domain.vo.EmailVO;
import com.su.enums.ResponseMsgEnum;
import com.su.pojo.Account;
import com.su.mapper.AccountMapper;
import com.su.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.utils.ConstantPropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2023-12-19
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 发送邮箱验证码
     * @param emailVO
     * @return
     */
    @Override
    public Boolean sendEmsCode(EmailVO emailVO) {
        if (StringUtils.isEmpty(emailVO.getEmail())){
            throw new MyException(ResponseMsgEnum.EMAIL_NOT_NULL.code,ResponseMsgEnum.EMAIL_NOT_NULL.message);
        }
        //判断5分钟内是否已经获取过短信验证码
        String emailKey = RedisConstant.getEmailKey(emailVO.getEmail());
        String redisEmailCode = redisTemplate.opsForValue().get(emailKey);
        if(StringUtils.isNotEmpty(redisEmailCode)){
            throw new MyException(ResponseMsgEnum.EMAIL_SEND_ONE_MINUTE.code,ResponseMsgEnum.EMAIL_SEND_ONE_MINUTE.message);
        }
        //生成验证码
        StringBuilder resultCode=new StringBuilder();
        for (int i = 0; i < 4; i++) {
            Random random=new Random();
            resultCode.append(random.nextInt(10));
        }
        String code = resultCode.toString();
        //发送验证码
        sendCode(emailVO.getEmail(),code);
        //缓存到redis
        redisTemplate.opsForValue().set(emailKey,code,Long.parseLong(RedisConstant.EMAIL_TIMEOUT), TimeUnit.SECONDS);
        return true;
    }

    //发送验证码
    public void sendCode(String email,String code){
        try {
            SimpleEmail simpleEmail = new SimpleEmail();
            // 设置邮箱服务器信息
            simpleEmail.setHostName(ConstantPropertiesUtils.HOST);
            // 设置密码验证器
            simpleEmail.setAuthentication(ConstantPropertiesUtils.EMAIL, ConstantPropertiesUtils.PASSWORD);
            // 设置邮件发送者
            simpleEmail.setFrom(ConstantPropertiesUtils.EMAIL);
            //设置接收者
            simpleEmail.addTo(email);
            // 设置邮件编码
            simpleEmail.setCharset("UTF-8");
            // 设置邮件主题
            simpleEmail.setSubject("高校就业资源app-短信");
            // 设置邮件内容
            simpleEmail.setMsg("您的验证码为："+code+"，请勿告诉他人！！");
            // 设置邮件发送时间
            simpleEmail.setSentDate(new Date());
            // 发送邮件
            simpleEmail.send();
        } catch (EmailException e) {
            e.printStackTrace();
            throw new MyException(500 ,"邮箱验证码发送错误");
        }
    }


}
