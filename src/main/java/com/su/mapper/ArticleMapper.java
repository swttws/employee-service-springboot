package com.su.mapper;

import com.su.domain.pojo.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2023-12-26
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
