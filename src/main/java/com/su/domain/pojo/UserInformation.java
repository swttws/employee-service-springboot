package com.su.domain.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

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
 * @since 2024-03-12
 */
@TableName("user_information")
@ApiModel(value = "UserInformation对象", description = "")
@Data
public class UserInformation extends BaseEntity implements Serializable {

    @ApiModelProperty("用户名字")
    @TableField("name")
    private String name;

    @TableField("account_id")
    private Integer accountId;

    @ApiModelProperty("学校")
    @TableField("school")
    private String school;

    @ApiModelProperty("专业")
    @TableField("major")
    private String major;

    @ApiModelProperty("学校地址")
    @TableField("address")
    private String address;

    @ApiModelProperty("职业")
    @TableField("career")
    private String career;

    @ApiModelProperty("SCHOLAR博士，MASTER硕士，UNDERGRADUATE本科，TRAINING专科,OTHER其他学历")
    @TableField("education")
    private String education;

    @ApiModelProperty("投递数量")
    @TableField("deliver_number")
    private Integer deliverNumber;

    @ApiModelProperty("面试数量")
    @TableField("interview_number")
    private Integer interviewNumber;


}
