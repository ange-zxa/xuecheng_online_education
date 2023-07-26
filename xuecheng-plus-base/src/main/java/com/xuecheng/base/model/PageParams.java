package com.xuecheng.base.model;

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
    //当前页码
    private Long pageNo = 1L;
    //每页查询记录数
    private Long pageSize = 30L;
}
