package com.su.service.impl;

import com.su.common.exception.MyException;
import com.su.domain.pojo.Account;
import com.su.domain.pojo.Article;
import com.su.domain.vo.ArticleVO;
import com.su.mapper.ArticleMapper;
import com.su.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2023-12-26
 */
@Service
@Slf4j
public class ArticleServiceImp extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    /**
     * 图片上传
     * @return
     */
    @Override
    public byte[] upload(MultipartFile file) {
        byte[] bytes = null;
        try {
            bytes = file.getBytes();
        } catch (IOException ioException) {
            log.info("图片获取二进制流失败");
        }
        return bytes;
    }

    @Override
    public Integer saveOrUpdateArticle(ArticleVO articleVO) {
        if(StringUtils.isEmpty(articleVO.getContent()) ||StringUtils.isEmpty(articleVO.getTitle())){
            throw new MyException(500,"标题/内容不能为空");
        }
        Article article=baseMapper.selectById(articleVO.getId());
        //修改操作
        if(ObjectUtils.isNotEmpty(article)){
            article.setContent(articleVO.getContent());
            article.setTitle(articleVO.getTitle());
            baseMapper.updateById(article);
        }else{
            article=new Article();
            article.setContent(articleVO.getContent());
            article.setTitle(articleVO.getTitle());
            Account account = ThreadLocalUtil.getAccount();
            article.setUserId(String.valueOf(account.getId()));
            baseMapper.insert(article);
        }
        return article.getId();
    }
}



















