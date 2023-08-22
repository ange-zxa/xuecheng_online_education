package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/8/3
 * \* Time: 9:53
 * \* Description:
 * \
 */
@Api(value = "教师信息管理接口", tags = "教师信息管理管理接口")
@RestController
public class TeachplanController {
    @Autowired
    TeachplanService teachplanService;
    //查询课程计划
    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType = "Long",paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        return teachplanService.findTeachplanTree(courseId);
    }
    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }

    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{teachplanId}")
    public void deleteTeachplan(@PathVariable Long teachplanId) {
        teachplanService.deleteTeachplan(teachplanId);
    }

    /**
     *上移下移操作
     * @param moveType 向上或者向下移动
     * @param teachplanId 课程计划id
     */
    @ApiOperation("课程计划排序")
    @PostMapping("/teachplan/{moveType}/{teachplanId}")
    public void orderByTeachplan(@PathVariable String moveType, @PathVariable Long teachplanId) {
        teachplanService.orderByTeachplan(moveType,teachplanId);
    }
}
