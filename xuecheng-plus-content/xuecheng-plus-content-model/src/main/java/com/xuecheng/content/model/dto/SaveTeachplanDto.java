package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/8/3
 * \* Time: 15:11
 * \* Description:新增大章节，新增小章节，修改章节三个功能一个接口的模板类
 * \
 */
@Data
@ToString
public class SaveTeachplanDto {

    /***
     * 教学计划id
     */
    private Long id;

    /**
     * 课程计划名称
     */
    private String pname;

    /**
     * 课程计划父级Id
     */
    private Long parentid;

    /**
     * 层级，分为1、2、3级
     */
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    private String mediaType;


    /**
     * 课程标识
     */
    private Long courseId;

    /**
     * 课程发布标识
     */
    private Long coursePubId;


    /**
     * 是否支持试学或预览（试看）
     */
    private String isPreview;



}