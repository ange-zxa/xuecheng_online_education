package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/7/26
 * \* Time: 14:58
 * \* Description:分页查询模板类
 * \
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageParams {
    @ApiModelProperty(value = "页码")
    //当前页码
    private Long pageNo = 1L;
    @ApiModelProperty(value = "每页查询记录数")
    //每页查询记录数
    private Long pageSize = 30L;
}
