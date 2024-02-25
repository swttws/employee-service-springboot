package com.su.controller;


import com.su.common.response.ResultResponse;
import com.su.domain.pojo.Job;
import com.su.service.JobService;
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
@RequestMapping("/job")
@CrossOrigin
public class JobController {

    @Autowired
    private JobService jobService;

    @ApiOperation("保存或更新职位")
    @PostMapping("saveJob")
    public ResultResponse saveOrUpdateJob(@RequestBody Job job){
        return ResultResponse.success(jobService.saveOrUpdateJob(job));
    }

    @ApiOperation("获取职位列表")
    @GetMapping("listJob/{current}")
    public ResultResponse listJob(@PathVariable("current") Integer current){
        return ResultResponse.success(jobService.listJob(current));
    }

    @ApiOperation("删除职位")
    @GetMapping("deleteJob/{jobId}")
    public ResultResponse deleteJob(@PathVariable("jobId") Integer jobId){
        return ResultResponse.success(jobService.deleteJob(jobId));
    }

    @ApiOperation("职位发布与撤销发布")
    @GetMapping("operatorJob/{jobId}/{type}")
    public ResultResponse operatorJob(@PathVariable("jobId") Integer jobId
            ,@PathVariable("type") Integer type){
        return ResultResponse.success(jobService.operatorJob(jobId,type));
    }

    @ApiOperation("获取职位详情")
    @GetMapping("getById/{id}")
    public ResultResponse getById(@PathVariable("id") Integer id){
        return ResultResponse.success(jobService.getById(id));
    }

}

