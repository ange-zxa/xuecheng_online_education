package com.xuecheng.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/7/26
 * \* Time: 15:10
 * \* Description:分类查询结果模板类
 * \
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    // 数据列表
    private List<T> items;

    //总记录数
    private long counts;

    //当前页码
    private long page;

    //每页记录数
    private long pageSize;

}