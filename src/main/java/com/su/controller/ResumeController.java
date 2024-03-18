package com.su.controller;


import com.su.common.response.ResultResponse;
import com.su.domain.pojo.Resume;
import com.su.service.ResumeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-03-18
 */
@RestController
@RequestMapping("/resume")
@CrossOrigin
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @ApiOperation("投递简历")
    @PostMapping("/deliver")
    public ResultResponse deliver(@RequestBody Resume resume){
        return ResultResponse.success(resumeService.deliver(resume));
    }

    @ApiOperation("查询简历列表")
    @GetMapping("/getResume")
    public ResultResponse getResume(){
        return ResultResponse.success(resumeService.getResume());
    }

    @ApiOperation("更改简历状态")
    @GetMapping("updateResume/{resumeId}/{type}")
    public ResultResponse updateResume(@PathVariable("resumeId") Integer resumeId,
                                       @PathVariable("type") Integer type){
        Resume resume = resumeService.getById(resumeId);
        resume.setStatus(type);
        resumeService.updateById(resume);
        return ResultResponse.success();
    }

}

