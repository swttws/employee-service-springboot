package com.su.service;

import com.su.domain.es.ArticleEsEntity;
import com.su.domain.pojo.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.su.domain.vo.ArticleDataVO;
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
    SearchVO search(String query);

    /**
     * 过滤敏感词
     * @param word
     * @return
     */
    String filterWord(String word);

    /**
     * 添加搜索记录
     * @param word
     */
    Boolean addSearchWord(String word);

    /**
     * 删除搜索记录
     *
     * @return
     */
    Boolean deleteSearchWord();

    /**
     * 展示历史搜索记录
     * @return
     */
    List<String> getSearchWord(Integer type);

    /**
     * 文章详情访问
     * @param id
     * @return
     */
    Article getByIdAndAdd(Integer id);

    /**
     * 获取文章信息数据，文章列表
     * @return
     */
    ArticleDataVO getCreateData();

    /**
     * 获取热门文章数据，前五
     * @return
     */
    List<Article> getHotArticle();

    /**
     * 删除文章
     * @param id
     * @return
     */
    Boolean deleteById(Integer id);
}
