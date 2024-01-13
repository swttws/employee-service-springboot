package com.su.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.su.domain.pojo.Account;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author swt
 */
@Slf4j
public class JwtUtils {

    /**
     * 默认七天过期
     */
    private static final long TIME_OUT = 4 * 24 * 60 * 60 * 1000;

    /**
     * 生成token
     *
     * @param userView
     * @return
     */
    public static String createToken(Account userView) {
        try {
            Date date = new Date(System.currentTimeMillis() + TIME_OUT);
            //生成签名
            Algorithm algorithm = Algorithm.HMAC256(userView.getPassword());
            return JWT.create()
                    //添加用户名到token中
                    .withClaim("username", userView.getUsername())
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            log.info("生成token异常");
            return null;
        }
    }

    /**
     * 通过token获取用户名
     *
     * @param token
     * @return
     */
    public static String getUserName(String token) {
        try {
            DecodedJWT decode = JWT.decode(token);
            return decode.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            log.info("解析token失败");
            return null;
        }
    }

    /**
     * 校验token
     *
     * @param token
     * @param userView
     * @return
     */
    public static boolean verify(String token, Account userView) {
        try {
            //校验是否有效
            Algorithm algorithm = Algorithm.HMAC256(userView.getPassword());
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", userView.getUsername())
                    .build();
            DecodedJWT verify = verifier.verify(token);
            //校验是否过期
            DecodedJWT decode = JWT.decode(token);
            long tokenTime = decode.getExpiresAt().getTime();
            long nowTime = System.currentTimeMillis();
            if (tokenTime < nowTime) {
                log.info("token过期");
                return false;
            }
            log.info("token正常");
            return true;
        } catch (UnsupportedEncodingException e) {
            log.info("token异常");
            return false;
        }
    }
}
