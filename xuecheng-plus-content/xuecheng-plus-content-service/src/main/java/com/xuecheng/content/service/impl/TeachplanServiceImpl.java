package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        Long id = teachplanDto.getId();
        if (id == null) {
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachplanDto, teachplan);
            //确认排序字段，找到同级节点，如果为空则置为1

            LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
            Long courseId = teachplanDto.getCourseId();
            Long parentid = teachplanDto.getParentid();
            LambdaQueryWrapper<Teachplan> eq = teachplanLambdaQueryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid);
            Integer integer = teachplanMapper.selectCount(eq);
            int i = integer + 1;
            teachplan.setOrderby(i);
            teachplanMapper.insert(teachplan);
        } else {
            //保存修改
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Transactional
    @Override
    public void deleteTeachplan(Long teachplanId) {
        //判断参数不能为空
        if (teachplanId == null) {
            XueChengPlusException.cast("课程计划id不能为空");
        }
        //判断是章还是节
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        Long parentid = teachplan.getParentid();
        if (parentid == 0) {
            LambdaQueryWrapper<Teachplan> tl = new LambdaQueryWrapper<>();
            tl.eq(Teachplan::getParentid, teachplanId);
            Integer integer = teachplanMapper.selectCount(tl);
            if (integer <= 0) {
                teachplanMapper.deleteById(teachplanId);
            } else {
                XueChengPlusException.cast("先删除完其下面节");
            }
        } else {
            //为节
            teachplanMapper.deleteById(teachplanId);
            LambdaQueryWrapper<TeachplanMedia> teachplanMediaLambdaQueryWrapper = new LambdaQueryWrapper<TeachplanMedia>();
            teachplanMediaLambdaQueryWrapper.eq(TeachplanMedia::getTeachplanId, teachplanId);
            teachplanMediaMapper.delete(teachplanMediaLambdaQueryWrapper);
        }
    }

    @Override
    public void orderByTeachplan(String moveType, Long teachplanId) {
        //获取层级和当前orderby
        Teachplan t = teachplanMapper.selectById(teachplanId);
        //获取层级，看是大章节还是小章节
        Integer grade = t.getGrade();
        //获取父id
        Long parentid = t.getParentid();
        //获取课程编号
        Long courseId = t.getCourseId();
        //获取当前的排序序号
        Integer orderby = t.getOrderby();
        //判断逻辑，如果是大章节上移
        if ("moveup".equals(moveType)) {
            if (grade == 1) {
                LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
                LambdaQueryWrapper<Teachplan> last = teachplanLambdaQueryWrapper.eq(Teachplan::getCourseId, courseId)
                        .eq(Teachplan::getGrade, grade)
                        .lt(Teachplan::getOrderby, orderby)
                        .orderByDesc(Teachplan::getOrderby)
                        .last("limit 1");
                Teachplan teachplan = teachplanMapper.selectOne(last);
                exchangeOrderby(t, teachplan);
            } else {
                //小节下移
                LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
                LambdaQueryWrapper<Teachplan> last = teachplanLambdaQueryWrapper.eq(Teachplan::getParentid, parentid)
                        .lt(Teachplan::getOrderby, orderby)
                        .orderByDesc(Teachplan::getOrderby)
                        .last("limit 1");
                Teachplan teachplan = teachplanMapper.selectOne(last);
                exchangeOrderby(t,teachplan);
            }
        }else {
            //下移大章节和小章节
            if (grade == 1) {
                LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
                LambdaQueryWrapper<Teachplan> last = teachplanLambdaQueryWrapper.eq(Teachplan::getCourseId, courseId)
                        .eq(Teachplan::getGrade, grade)
                        .gt(Teachplan::getOrderby, orderby)
                        .orderByAsc(Teachplan::getOrderby)
                        .last("limit 1");
                Teachplan teachplan = teachplanMapper.selectOne(last);
                exchangeOrderby(t, teachplan);
            } else {
                //小节下移
                LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
                LambdaQueryWrapper<Teachplan> last = teachplanLambdaQueryWrapper.eq(Teachplan::getParentid, parentid)
                        .gt(Teachplan::getOrderby, orderby)
                        .orderByAsc(Teachplan::getOrderby)
                        .last("limit 1");
                Teachplan teachplan = teachplanMapper.selectOne(last);
                exchangeOrderby(t,teachplan);
            }
        }

    }

    public void exchangeOrderby(Teachplan teachplan1, Teachplan teachplan2) {
        if (teachplan2 == null) {
            XueChengPlusException.cast("到头了不能再上移");
        }
        Integer orderby1 = teachplan1.getOrderby();
        Integer orderby2 = teachplan2.getOrderby();
        Integer temp = orderby1;
        teachplan1.setOrderby(orderby2);
        teachplan2.setOrderby(temp);
        teachplanMapper.updateById(teachplan1);
        teachplanMapper.updateById(teachplan2);

    }
}
