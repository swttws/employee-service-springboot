package com.su.domain.vo;

import lombok.Data;

/**
 * @author suweitao
 */
@Data
public class QueryJobVO {

    /**
     * 模糊条件
     */
    private String condition;

    /**
     * 薪资区间
     */
    private String salaryRange;

    /**
     * 职位类型
     */
    private String type;

    /**
     * 职位性质
     */
    private String jobProperties;

}
