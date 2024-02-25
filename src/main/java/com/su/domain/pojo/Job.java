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
 * @since 2024-02-24
 */
@TableName("job")
@ApiModel(value = "Job对象", description = "")
@Data
public class Job extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("公司id")
    @TableField("company_id")
    private String companyId;

    @ApiModelProperty("职位名称")
    @TableField("job_name")
    private String jobName;

    @ApiModelProperty("最低薪资")
    @TableField("min_salary")
    private Integer minSalary;

    @ApiModelProperty("最大薪资")
    @TableField("max_salary")
    private Integer maxSalary;

    @ApiModelProperty("职位福利")
    @TableField("welfare")
    private String welfare;

    @ApiModelProperty("职位介绍")
    @TableField("description")
    private String description;

    @ApiModelProperty("职位类型")
    @TableField("type")
    private String type;

    @ApiModelProperty("职位地点")
    @TableField("address")
    private String address;

    @ApiModelProperty("职位性质")
    @TableField("job_properties")
    private String jobProperties;

    @ApiModelProperty("是否发布，1发布，2未发布")
    @TableField("is_send")
    private Integer isSend;

}
