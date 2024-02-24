package com.su.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.domain.vo.OperatorVO;
import com.su.domain.pojo.Account;
import com.su.domain.pojo.Article;
import com.su.domain.pojo.ArticleRelation;
import com.su.mapper.ArticleRelationMapper;
import com.su.mq.producer.RabbitProducer;
import com.su.service.AccountService;
import com.su.service.ArticleRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.service.ArticleService;
import com.su.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-02-24
 */
@Service
public class ArticleRelationServiceImp extends ServiceImpl<ArticleRelationMapper, ArticleRelation> implements ArticleRelationService {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RabbitProducer rabbitProducer;

    /**
     * 文章收藏、点赞、评论保存
     *
     * @param operatorVO
     * @return
     */
    @Override
    public Boolean operatorArticle(OperatorVO operatorVO) {
        Account account = ThreadLocalUtil.getAccount();
        Integer userId = account.getId();
        String username = account.getUsername();
        //获取文章
        Article article = articleService.getById(operatorVO.getArticleId());
        //删除操作
        if (!operatorVO.getIsAdd()) {
            baseMapper.delete(Wrappers.<ArticleRelation>lambdaQuery()
                    .eq(ArticleRelation::getArticleId, operatorVO.getArticleId())
                    .eq(ArticleRelation::getUserId, userId)
                    .eq(ArticleRelation::getType, operatorVO.getType()));
            return true;
        }
        //添加操作
        ArticleRelation articleRelation = new ArticleRelation();
        articleRelation.setArticleId(operatorVO.getArticleId());
        articleRelation.setArticleUserId(article.getUserId());
        articleRelation.setType(operatorVO.getType());
        articleRelation.setUserId(userId);
        articleRelation.setUserName(username);
        articleRelation.setComment(operatorVO.getComment());
        baseMapper.insert(articleRelation);
        //更新es和mysql
        updateArticle(article, operatorVO.getType(), operatorVO.getIsAdd());
        return true;
    }

    /**
     * 更新文章数据
     *
     * @param article
     * @param type
     * @param isAdd
     */
    private void updateArticle(Article article, Integer type, Boolean isAdd) {
        //1点赞，2.评论，3、收藏
        if (type == 1) {
            Integer praiseNum = dealVale(article.getPraiseNum());
            article.setPraiseNum(isAdd ? praiseNum + 1 : praiseNum > 0 ? praiseNum - 1 : 0);
        } else if (type == 2) {
            Integer commentNum = dealVale(article.getCommentNum());
            article.setCommentNum(isAdd ? commentNum + 1 : commentNum > 0 ? commentNum - 1 : 0);
        } else {
            Integer collectNum = dealVale(article.getCollectNum());
            article.setCollectNum(isAdd ? collectNum + 1 : collectNum > 0 ? collectNum - 1 : 0);
        }
        articleService.updateById(article);
        rabbitProducer.send(article);
    }

    private Integer dealVale(Integer value) {
        if (ObjectUtils.isNull(value)) {
            return 0;
        }
        return value;
    }

    /**
     * 判断是否点赞收藏
     *
     * @param articleId
     * @return
     */
    @Override
    public List<Boolean> isOperator(Integer articleId) {
        List<Boolean> result = Arrays.asList(false, false);
        List<ArticleRelation> articleRelations = baseMapper.selectList(Wrappers.<ArticleRelation>lambdaQuery()
                .eq(ArticleRelation::getArticleId, articleId)
                .eq(ArticleRelation::getUserId, ThreadLocalUtil.getAccount().getId()));
        for (ArticleRelation articleRelation : articleRelations) {
            //点赞过
            if (articleRelation.getType() == 1) {
                result.set(0, true);
            }
            //收藏过
            if (articleRelation.getType() == 3) {
                result.set(1, true);
            }
        }
        return result;
    }

    /**
     * 获取文章评论
     *
     * @param articleId
     * @return
     */
    @Override
    public List<ArticleRelation> getComment(Integer articleId) {
        return baseMapper.selectList(Wrappers.<ArticleRelation>lambdaQuery()
                .eq(ArticleRelation::getType, 2)
                .eq(ArticleRelation::getArticleId, articleId));
    }
}






