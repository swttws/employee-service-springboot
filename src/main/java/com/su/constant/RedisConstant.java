package com.su.constant;

/**
 * @author suweitao
 */
public class RedisConstant {

    /**
     * 邮箱验证码key
     */
    private static final String EMAIL_CODE_KEY_PREFIX = "email:code:";
    public static final String EMAIL_TIMEOUT = "60";

    public static String getEmailKey(String email) {
        return EMAIL_CODE_KEY_PREFIX + email;
    }

}
