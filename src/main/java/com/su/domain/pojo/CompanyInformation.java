package com.su.domain.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.su.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-02-24
 */
@TableName("company_information")
@ApiModel(value = "CompanyInformation对象", description = "")
@Data
public class CompanyInformation extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("公司名")
    @TableField("company_name")
    private String companyName;

    @ApiModelProperty("公司总人数")
    @TableField("total_num")
    private Integer totalNum;

    @ApiModelProperty("电话")
    @TableField("phone")
    private String phone;

    @ApiModelProperty("类型")
    @TableField("type")
    private String type;

    @ApiModelProperty("成立时间")
    @TableField("setup_time")
    private LocalDateTime setupTime;

    @ApiModelProperty("平均薪资")
    @TableField("salary_avg")
    private BigDecimal salaryAvg;

    @ApiModelProperty("老板名字")
    @TableField("boss_name")
    private String bossName;

    @ApiModelProperty("描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("位置")
    @TableField("address")
    private String address;
}
