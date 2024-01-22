package com.su.service;

import com.su.domain.es.ArticleEsEntity;
import com.su.domain.pojo.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.su.domain.vo.ArticleVO;
import com.su.domain.vo.SearchVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2023-12-26
 */
public interface ArticleService extends IService<Article> {

    /**
     * 图片上传
     * @return
     */
    byte[] upload(MultipartFile file);

    /**
     * 保存或修改文章
     * @param articleVO
     * @return
     */
    Integer saveOrUpdateArticle(ArticleVO articleVO);

    /**
     * 文章发布
     * @param articleVO
     */
    void sendArticle(ArticleVO articleVO);

    /**
     * 首页文章显示
     * @return
     */
    SearchVO search();
}
