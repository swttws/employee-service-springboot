package com.su.service.impl;

import com.su.common.exception.MyException;
import com.su.domain.pojo.Account;
import com.su.domain.pojo.Article;
import com.su.domain.vo.ArticleVO;
import com.su.mapper.ArticleMapper;
import com.su.mq.producer.RabbitProducer;
import com.su.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2023-12-26
 */
@Service
@Slf4j
public class ArticleServiceImp extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private RabbitProducer rabbitProducer;

    /**
     * 图片上传
     *
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
        if (StringUtils.isEmpty(articleVO.getContent()) || StringUtils.isEmpty(articleVO.getTitle())) {
            throw new MyException(500, "标题/内容不能为空");
        }
        Article article = baseMapper.selectById(articleVO.getId());
        //修改操作
        if (ObjectUtils.isNotEmpty(article)) {
            article.setContent(articleVO.getContent());
            article.setTitle(articleVO.getTitle());
            baseMapper.updateById(article);
        } else {
            article = new Article();
            article.setIsDeleted(true);
            article.setContent(articleVO.getContent());
            article.setTitle(articleVO.getTitle());
            Account account = ThreadLocalUtil.getAccount();
            article.setUserId(String.valueOf(account.getId()));
            baseMapper.insert(article);
        }
        return article.getId();
    }

    /**
     * 文章发布或修改
     *
     * @param articleVO
     */
    @Override
    public void sendArticle(ArticleVO articleVO) {
        if (ObjectUtils.isEmpty(articleVO.getGroupName()) || ObjectUtils.isEmpty(articleVO.getSendTime())
                || ObjectUtils.isEmpty(articleVO.getType())) {
            throw new MyException(500, "文章类型、时间或权限不能为空");
        }
        //查询文章
        Article article = baseMapper.selectById(articleVO.getId());
        if(Objects.isNull(article)){
            throw new MyException(500,"系统异常,请联系管理员");
        }
        BeanUtils.copyProperties(articleVO,article);
        //设置为发布状态
        article.setIsDeleted(false);
        baseMapper.updateById(article);
        //通知mq，将文章数据写入es
        rabbitProducer.send(article,String.valueOf(article.getId()));
    }
}



















