package com.su.controller;


import com.su.common.response.ResultResponse;
import com.su.domain.vo.ArticleVO;
import com.su.service.ArticleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2023-12-26
 */
@RestController
@RequestMapping("/article")
@CrossOrigin
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/upload")
    @ApiOperation("图片上传")
    public ResultResponse upload(MultipartFile file){
        return ResultResponse.success(articleService.upload(file));
    }

    @PostMapping("saveOrUpdate")
    @ApiOperation("文章信息保存或修改,第一步")
    public ResultResponse saveOrUpdate(@RequestBody ArticleVO articleVO){
        return ResultResponse.success(articleService.saveOrUpdateArticle(articleVO));
    }



}

