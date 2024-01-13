package com.su.controller;


import com.su.common.response.ResultResponse;
import com.su.domain.dto.CatagoryDTO;
import com.su.domain.pojo.Catagory;
import com.su.service.CatagoryService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-12-18
 * @since 2024-01-13
 */
@RestController
@RequestMapping("/catagory")
@CrossOrigin
public class CatagoryController {

    @Autowired
    private CatagoryService catagoryService;

    @ApiOperation("查询所有有分类")
    @GetMapping("/getAll")
    public ResultResponse getAll() {
        List<Catagory> catagoryList = catagoryService.list();
        if (ObjectUtils.isNotEmpty(catagoryList)) {
            return ResultResponse.success(catagoryList.stream().map(catagory -> {
                CatagoryDTO catagoryDTO = new CatagoryDTO();
                catagoryDTO.setValue(catagory.getId());
                catagoryDTO.setText(catagory.getGroupName());
                return catagoryDTO;
            }).filter(ObjectUtils::isNotEmpty).collect(Collectors.toList()));
        } else {
            return ResultResponse.success();
        }
    }

}

