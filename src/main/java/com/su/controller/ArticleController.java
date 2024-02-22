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

    @ApiOperation("文章发布，第二步骤")
    @PostMapping("sendArticle")
    public ResultResponse sendArticle(@RequestBody ArticleVO articleVO){
        articleService.sendArticle(articleVO);
        return ResultResponse.success();
    }

    @ApiOperation("根据id查询文章信息")
    @GetMapping("getById/{id}")
    public ResultResponse getById(@PathVariable("id") Integer id){
        return ResultResponse.success(articleService.getById(id));
    }

    @ApiOperation("首页文章展示")
    @GetMapping("search")
    public ResultResponse search(@RequestParam("query") String query){
        return ResultResponse.success(articleService.search(query));
    }

    @ApiOperation("敏感词过滤")
    @GetMapping("/filterWord")
    public ResultResponse filterWord(@RequestParam("word") String word){
        return ResultResponse.success(articleService.filterWord(word));
    }

    @ApiOperation("添加历史搜索记录")
    @GetMapping("addSearchWord")
    public ResultResponse addSearchWord(@RequestParam("word") String word){
        return ResultResponse.success(articleService.addSearchWord(word));
    }

    @ApiOperation("删除历史搜索记录")
    @GetMapping("deleteSearchWord")
    public ResultResponse deleteSearchWord(){
        return ResultResponse.success(articleService.deleteSearchWord());
    }

    @ApiOperation("展示历史搜索记录")
    @GetMapping("getSearchWord")
    public ResultResponse getSearchWord(@RequestParam("type") Integer type){
        return ResultResponse.success(articleService.getSearchWord(type));
    }

    @ApiOperation("根据id查询文章信息，添加浏览量")
    @GetMapping("getByIdAndAdd/{id}")
    public ResultResponse getByIdAndAdd(@PathVariable("id") Integer id){
        return ResultResponse.success(articleService.getByIdAndAdd(id));
    }

}

