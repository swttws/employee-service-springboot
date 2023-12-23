package com.su.enums;

/**
 * @author suweitao
 */
public enum ResponseMsgEnum {

    /**
     * account相关
     */
    EMAIL_NOT_NULL(100,"邮箱不能为空"),
    EMAIL_SEND_ONE_MINUTE(101,"邮箱一分钟内已经发送过验证码"),
    NOT_NULL(102,"用户名，密码或邮箱不能为空")
    ;

    public Integer code;

    public String message;

    ResponseMsgEnum(Integer code, String message){
        this.code=code;
        this.message=message;
    }
}
