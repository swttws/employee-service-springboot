package com.su.domain.vo;

import lombok.Data;

/**
 * @author suweitao
 */
@Data
public class OperatorVO {

    /**
     * 操作类型,1点赞，2.评论，3、收藏
     */
    private Integer type;

    /**
     * 文章id
     */
    private Integer articleId;

    /**
     * 操作类型，删除或添加
     */
    private Boolean isAdd;

    /**
     * 评论内容
     */
    private String comment;

}
