package com.su.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.common.exception.MyException;
import com.su.constant.RedisConstant;
import com.su.domain.pojo.CompanyInformation;
import com.su.domain.vo.EmailVO;
import com.su.domain.vo.LoginVO;
import com.su.domain.vo.RegisterVO;
import com.su.enums.ResponseMsgEnum;
import com.su.domain.pojo.Account;
import com.su.mapper.AccountMapper;
import com.su.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.service.CompanyInformationService;
import com.su.utils.ConstantPropertiesUtils;
import com.su.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2023-12-19
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CompanyInformationService companyInformationService;

    /**
     * 发送邮箱验证码
     *
     * @param emailVO
     * @return
     */
    @Override
    public Boolean sendEmsCode(EmailVO emailVO) {
        if (StringUtils.isEmpty(emailVO.getEmail())) {
            throw new MyException(ResponseMsgEnum.EMAIL_NOT_NULL.code, ResponseMsgEnum.EMAIL_NOT_NULL.message);
        }
        //判断5分钟内是否已经获取过短信验证码
        String emailKey = RedisConstant.getEmailKey(emailVO.getEmail());
        String redisEmailCode = redisTemplate.opsForValue().get(emailKey);
        if (StringUtils.isNotEmpty(redisEmailCode)) {
            throw new MyException(ResponseMsgEnum.EMAIL_SEND_ONE_MINUTE.code, ResponseMsgEnum.EMAIL_SEND_ONE_MINUTE.message);
        }
        //生成验证码
        StringBuilder resultCode = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            Random random = new Random();
            resultCode.append(random.nextInt(10));
        }
        String code = resultCode.toString();
        //发送验证码
        sendCode(emailVO.getEmail(), code);
        //缓存到redis
        redisTemplate.opsForValue().set(emailKey, code, Long.parseLong(RedisConstant.EMAIL_TIMEOUT), TimeUnit.SECONDS);
        return true;
    }

    //发送验证码
    public void sendCode(String email, String code) {
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
            simpleEmail.setMsg("您的验证码为：" + code + "，请勿告诉他人！！");
            // 设置邮件发送时间
            simpleEmail.setSentDate(new Date());
            // 发送邮件
            simpleEmail.send();
        } catch (EmailException e) {
            e.printStackTrace();
            throw new MyException(500, "邮箱验证码发送错误");
        }
    }

    /**
     * 注册
     *
     * @param registerVO
     * @return
     */
    @Override
    public Boolean register(RegisterVO registerVO) {
        String companyName = registerVO.getCompanyName();
        if (StringUtils.isEmpty(registerVO.getUsername()) || StringUtils.isEmpty(registerVO.getPassword())
                || StringUtils.isEmpty(registerVO.getEmail())) {
            throw new MyException(ResponseMsgEnum.NOT_NULL.code, ResponseMsgEnum.NOT_NULL.message);
        }
        //判断用户名是否重复
        Long count = baseMapper.selectCount(Wrappers.<Account>lambdaQuery()
                .eq(Account::getUsername, registerVO.getUsername()).or()
                .eq(Account::getEmail, registerVO.getEmail()));
        if (count > 0) {
            throw new MyException(500, "用户名或邮箱已经注册过");
        }
        //验证验证码
        String redisCode = redisTemplate.opsForValue().get(RedisConstant.getEmailKey(registerVO.getEmail()));
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(registerVO.getCode())) {
            throw new MyException(500, "验证码错误或过期");
        }
        //保存注册用户信息
        Account account = new Account();
        BeanUtils.copyProperties(registerVO, account);
        String salt = generatorSalt();
        account.setSalt(salt);
        account.setPassword(DigestUtils.md5DigestAsHex((registerVO.getPassword() + salt).getBytes()));
        account.setType(StringUtils.isEmpty(companyName) ? 1 : 2);
        account.setCreateTime(new Date());

        //若是公司注册
        if (StringUtils.isNotEmpty(companyName)) {
            long counts = companyInformationService.count(Wrappers.<CompanyInformation>lambdaQuery()
                    .eq(CompanyInformation::getCompanyName, companyName));
            if (counts > 0) {
                throw new MyException(500, "公司名不能重复");
            }
            CompanyInformation companyInformation = new CompanyInformation();
            companyInformation.setCompanyName(companyName);
            companyInformationService.save(companyInformation);
            account.setThirdId(companyInformation.getId());
        }
        baseMapper.insert(account);
        return true;
    }

    /**
     * 生成盐加密密码
     *
     * @return
     */
    private String generatorSalt() {
        String[] str = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n"};
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(14);
            result.append(str[index]);
        }
        return result.toString();
    }

    @Override
    public String login(LoginVO loginVO) {
        //数据校验
        String username = loginVO.getUsername();
        String password = loginVO.getPassword();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new MyException(500, "用户名或密码不能为空");
        }
        //验证信息
        Account account = baseMapper.selectOne(Wrappers.<Account>lambdaQuery().eq(Account::getUsername, username));
        if (Objects.isNull(account)) {
            throw new MyException(500, "用户不存在");
        }
        //验证密码
        String newPasswd = DigestUtils.md5DigestAsHex((password + account.getSalt()).getBytes());
        if (!newPasswd.equals(account.getPassword())) {
            throw new MyException(500, "用户名或密码错误");
        }
        //生成token
        String token = JwtUtils.createToken(account);
        if (StringUtils.isEmpty(token)) {
            throw new MyException(500, "系统异常，请稍后重新登录");
        }
        return token;
    }
}
