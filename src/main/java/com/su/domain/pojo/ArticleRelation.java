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
@TableName("article_relation")
@ApiModel(value = "ArticleRelation对象", description = "")
@Data
public class ArticleRelation extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("文章id")
    @TableField("article_id")
    private Integer articleId;

    @ApiModelProperty("文章作者id")
    @TableField("article_user_id")
    private String articleUserId;

    @ApiModelProperty("操作用户名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("操作用户id")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty("类型，1评论，2点赞，3收藏")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("评论内容")
    @TableField("comment")
    private String comment;

}
