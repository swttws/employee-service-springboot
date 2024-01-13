package com.su.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @author suweitao
 */
@Data
public class CatagoryDTO {


    @ApiModelProperty("分类名称")
    private String text;

    @ApiModelProperty("分类id")
    private Integer value;

}
