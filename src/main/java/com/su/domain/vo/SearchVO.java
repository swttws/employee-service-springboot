package com.su.domain.vo;

import com.su.domain.es.ArticleEsEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author suweitao
 */
@Data
public class SearchVO implements Serializable {

    @ApiModelProperty("文章数据")
    private List<ArticleEsEntity> articleEsEntityList;

    @ApiModelProperty("首页文章分类标签")
    private List<Map<String,String>> groupNameList;

}
