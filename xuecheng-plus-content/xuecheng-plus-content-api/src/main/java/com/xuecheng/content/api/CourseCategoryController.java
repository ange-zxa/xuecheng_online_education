package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/7/28
 * \* Time: 11:35
 * \* Description:课程分类接口
 * \
 */
@Api(value = "课程分类管理接口", tags = "课程分类管理接口")
@RestController
public class CourseCategoryController {
    @Autowired
    CourseCategoryService courseCategoryService;
    @GetMapping("course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
         return courseCategoryService.queryTreeNodes("1");
    }


}
