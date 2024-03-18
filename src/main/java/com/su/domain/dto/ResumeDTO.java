package com.su.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.su.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-03-18
 */

@ApiModel(value = "Resume对象", description = "")
@Data
public class ResumeDTO  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("简历二进制")
    private String resume;

    @ApiModelProperty("时间")
    private Date time;

    private String jobName;

    @ApiModelProperty("学校")
    private String school;

    private Integer userId;

    private Integer jobId;

    private Integer companyId;

    private Integer status;


}
