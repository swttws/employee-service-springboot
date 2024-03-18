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
 * @since 2024-03-18
 */
@TableName("resume")
@ApiModel(value = "Resume对象", description = "")
@Data
public class Resume extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("公司id")
    @TableField("company_id")
    private Integer companyId;

    @ApiModelProperty("个人id")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty("简历二进制")
    @TableField("resume")
    private String resume;

    @ApiModelProperty("状态，0未处理，1已通过，2未通过")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("职位id")
    @TableField("job_id")
    private Integer jobId;


}
