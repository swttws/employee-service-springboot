package com.su.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author suweitao
 */
@Data
public class ArticleVO {

    private Integer id;

    @ApiModelProperty("文章标题")
    @TableField("title")
    private String title;

    @ApiModelProperty("文章内容")
    @TableField("content")
    private String content;

    @ApiModelProperty("文章类型")
    @TableField("type")
    private String type;

    @ApiModelProperty("文章发布时间")
    @TableField("send_time")
    private LocalDateTime sendTime;

    @ApiModelProperty("文章封面")
    @TableField("cover")
    private String cover;

    @ApiModelProperty("分类id")
    @TableField("group_id")
    private Integer groupId;

    @ApiModelProperty("分类名称")
    @TableField("group_name")
    private String groupName;

}
