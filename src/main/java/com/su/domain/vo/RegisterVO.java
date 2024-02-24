package com.su.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author suweitao
 */
@Data
public class RegisterVO {

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("验证码")
    private String code;

    @ApiModelProperty("公司名")
    private String companyName;

}
