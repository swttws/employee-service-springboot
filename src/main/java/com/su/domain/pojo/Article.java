package com.su.domain.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

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
 * @since 2023-12-26
 */
@TableName("article")
@ApiModel(value = "Article对象", description = "")
@Data
public class Article extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户id")
    @TableField("user_id")
    private String userId;

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
    private Date sendTime;

    @ApiModelProperty("文章封面")
    @TableField("cover")
    private String cover;

    @ApiModelProperty("点赞数")
    @TableField("praise_num")
    private Integer praiseNum;

    @ApiModelProperty("收藏数")
    @TableField("collect_num")
    private Integer collectNum;

    @ApiModelProperty("评论数")
    @TableField("comment_num")
    private Integer commentNum;

    @ApiModelProperty("分类id")
    @TableField("group_id")
    private Integer groupId;

    @ApiModelProperty("分类名称")
    @TableField("group_name")
    private String groupName;

    @ApiModelProperty("浏览数")
    @TableField("view_num")
    private Integer viewNum;

    @TableField(exist = false)
    private String username;
}
