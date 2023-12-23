package com.su.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author suweitao
 */
@Data
public class EmailVO {

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("验证码，注册接口使用")
    private String code;

}
