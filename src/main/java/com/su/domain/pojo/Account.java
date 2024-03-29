package com.su.domain.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.su.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2023-12-19
 */
@TableName("account")
@ApiModel(value = "Account对象", description = "")
@Data
public class Account extends BaseEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("账号")
    @TableField("username")
    private String username;

    @ApiModelProperty("密码")
    @TableField("password")
    private String password;

    @ApiModelProperty("邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty("加盐字段，密码加密")
    @TableField("salt")
    private String salt;

    @ApiModelProperty("关联信息id")
    @TableField("third_id")
    private Integer thirdId;

    @ApiModelProperty("类型，1用户，2公司")
    @TableField("type")
    private Integer type;

}
