package com.su.domain.es;

import com.su.domain.es.annoation.EsIndex;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author suweitao
 */
@Document(indexName = "article_es_entity")
@EsIndex
@Component
@Data
public class ArticleEsEntity {

    @Id
    private Integer id;

    @ApiModelProperty("用户id")
    @Field(type = FieldType.Text)
    private String userId;

    @ApiModelProperty("文章标题")
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;

    @ApiModelProperty("文章内容")
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String content;

    @ApiModelProperty("文章类型")
    @Field(type = FieldType.Text)
    private String type;

    @ApiModelProperty("文章发布时间")
    @Field(type = FieldType.Date)
    private Date sendTime;

    @ApiModelProperty("点赞数")
    @Field(type = FieldType.Integer)
    private Integer praiseNum;

    @ApiModelProperty("收藏数")
    @Field(type = FieldType.Integer)
    private Integer collectNum;

    @ApiModelProperty("评论数")
    @Field(type = FieldType.Integer)
    private Integer commentNum;

    @ApiModelProperty("分类id")
    @Field(type = FieldType.Integer)
    private Integer groupId;

    @ApiModelProperty("分类名称")
    @Field(type = FieldType.Text)
    private String groupName;

    @ApiModelProperty("浏览数")
    @Field(type = FieldType.Integer)
    private Integer viewNum;
}
