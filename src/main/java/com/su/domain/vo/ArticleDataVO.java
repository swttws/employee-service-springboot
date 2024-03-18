package com.su.domain.vo;

import com.su.domain.pojo.Article;
import lombok.Data;

import java.util.List;

/**
 * @author suweitao
 */
@Data
public class ArticleDataVO {

    /**
     * 文章总数
     */
    private Integer articleTotal;

    /**
     * 点赞总数
     */
    private Integer praiseTotal;

    /**
     * 收藏总数
     */
    private Integer collectTotal;

    /**
     * 文章
     */
    private List<Article> articleList;

}
