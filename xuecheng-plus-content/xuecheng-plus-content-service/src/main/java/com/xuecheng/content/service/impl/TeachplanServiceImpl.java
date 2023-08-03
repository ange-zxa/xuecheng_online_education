package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/8/3
 * \* Time: 11:37
 * \* Description:
 * \
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        Long id = teachplanDto.getId();
        if(id == null){
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachplanDto,teachplan);
            //确认排序字段，找到同级节点，如果为空则置为1

            LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
            Long courseId = teachplanDto.getCourseId();
            Long parentid = teachplanDto.getParentid();
            LambdaQueryWrapper<Teachplan> eq = teachplanLambdaQueryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid);
            Integer integer = teachplanMapper.selectCount(eq);
            int i = integer + 1;
            teachplan.setOrderby(i);
            teachplanMapper.insert(teachplan);
        }else {
            //保存修改
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }
}
