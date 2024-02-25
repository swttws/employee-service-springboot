package com.su.domain.es;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.su.base.BaseEntity;
import com.su.domain.es.annoation.EsIndex;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.stereotype.Component;

import java.io.Serializable;

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
@EsIndex
@Component
public class JobEsEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @ApiModelProperty("公司id")
    @Field(type = FieldType.Text)
    private String companyId;

    @ApiModelProperty("职位名称")
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String jobName;

    @ApiModelProperty("最低薪资")
    @Field(type = FieldType.Integer)
    private Integer minSalary;

    @ApiModelProperty("最大薪资")
    @Field(type = FieldType.Integer)
    private Integer maxSalary;

    @ApiModelProperty("职位福利")
    @Field(type = FieldType.Text)
    private String welfare;

    @ApiModelProperty("职位介绍")
    @Field(type = FieldType.Text)
    private String description;

    @ApiModelProperty("职位类型")
    @Field(type = FieldType.Text)
    private String type;

    @ApiModelProperty("职位地点")
    @Field(type = FieldType.Text)
    private String address;

    @ApiModelProperty("职位性质")
    @Field(type = FieldType.Text)
    private String jobProperties;

    @ApiModelProperty("是否发布，1发布，2未发布")
    @Field(type = FieldType.Integer)
    private Integer isSend;

}
