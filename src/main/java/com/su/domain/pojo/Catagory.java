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
 * @since 2024-01-13
 */
@TableName("catagory")
@ApiModel(value = "Catagory对象", description = "")
@Data
public class Catagory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("分类名称")
    @TableField("group_name")
    private String groupName;

    @ApiModelProperty("父分类id,默认-1")
    @TableField("parent_id")
    private Integer parentId;

    @ApiModelProperty("分类类型")
    @TableField("type")
    private String type;

}
