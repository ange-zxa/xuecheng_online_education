package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
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
@RestController
public class CourseCategoryController {
    @GetMapping("course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        return null;
    }


}
