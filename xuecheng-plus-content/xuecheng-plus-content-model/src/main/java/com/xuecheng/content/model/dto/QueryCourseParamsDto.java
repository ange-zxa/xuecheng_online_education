package com.xuecheng.content.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/7/26
 * \* Time: 15:06
 * \* Description:课程查询擦参数模板类
 * \
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QueryCourseParamsDto {
    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;
}
