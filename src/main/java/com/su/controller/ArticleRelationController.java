package com.su.controller;


import com.su.common.response.ResultResponse;
import com.su.domain.vo.OperatorVO;
import com.su.service.ArticleRelationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-02-24
 */
@RestController
@RequestMapping("/articleRelation")
@CrossOrigin
public class ArticleRelationController {

    @Autowired
    private ArticleRelationService articleRelationService;


    @ApiOperation("用户评论、点赞、收藏保存")
    @PostMapping("operatorArticle")
    public ResultResponse operatorArticle(@RequestBody OperatorVO operatorVO){
        return ResultResponse.success(articleRelationService.operatorArticle(operatorVO));
    }

    @ApiOperation("判断是否已经点赞或收藏,list(点赞，收藏)")
    @GetMapping("isOperator/{articleId}")
    public ResultResponse isOperator(@PathVariable("articleId") Integer articleId){
        return ResultResponse.success(articleRelationService.isOperator(articleId));
    }

    @ApiOperation("获取文章评论")
    @GetMapping("getComment/{articleId}")
    public ResultResponse getComment(@PathVariable("articleId") Integer articleId){
        return ResultResponse.success(articleRelationService.getComment(articleId));
    }

}

