package com.su.service;

import com.su.domain.vo.OperatorVO;
import com.su.domain.pojo.ArticleRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-02-24
 */
public interface ArticleRelationService extends IService<ArticleRelation> {

    /**
     * 文章收藏、点赞、评论保存
     * @param operatorVO
     * @return
     */
    Boolean operatorArticle(OperatorVO operatorVO);

    /**
     * 判断是否点赞，收藏
     * @param articleId
     * @return
     */
    List<Boolean> isOperator(Integer articleId);

    /**
     * 获取文章评论
     * @param articleId
     * @return
     */
    List<ArticleRelation> getComment(Integer articleId);
}
