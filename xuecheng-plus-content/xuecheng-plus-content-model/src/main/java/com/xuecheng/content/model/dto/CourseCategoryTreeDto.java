package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/7/28
 * \* Time: 11:31
 * \* Description: 课程分类dto目模板
 * \
 */
@Data

public class CourseCategoryTreeDto extends CourseCategory {
    List<CourseCategoryTreeDto> ChildrenTreeNodes;
}
