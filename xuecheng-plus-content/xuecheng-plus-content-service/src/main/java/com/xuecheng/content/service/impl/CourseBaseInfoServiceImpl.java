package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/7/27
 * \* Time: 15:27
 * \* Description:
 * \
 */
@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;


    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //拼接查询条件
        //根据课程名称模糊查询  name like '%名称%'
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
        //根据课程审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        //分页参数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        //分页查询E page 分页参数, @Param("ew") Wrapper<T> queryWrapper 查询条件
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        //数据
        List<CourseBase> items = pageResult.getRecords();
        //总记录数
        long total = pageResult.getTotal();
        //准备返回数据 List<T> items, long counts, long page, long pageSize
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
        return courseBasePageResult;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        //校验数据
//        if (StringUtils.isBlank(dto.getName())) {
//            XueChengPlusException.cast("课程名称为空");
//            XueChengPlusException.cast("课程名称为空");
//        }
//        if (StringUtils.isBlank(dto.getMt())) {
//            XueChengPlusException.cast("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getSt())) {
//            throw new RuntimeException("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getGrade())) {
//            throw new RuntimeException("课程等级为空");
//        }
//
//        if (StringUtils.isBlank(dto.getTeachmode())) {
//            throw new RuntimeException("教育模式为空");
//        }
//
//        if (StringUtils.isBlank(dto.getUsers())) {
//            throw new RuntimeException("适应人群为空");
//        }
//
//        if (StringUtils.isBlank(dto.getCharge())) {
//            throw new RuntimeException("收费规则为空");
//        }用jSR在controller层校验
        //向课程基本信息表插入信息course_base
        CourseBase courseBase = new CourseBase();
//        用bean工具类进行复制，只要属性名称一样就可以拷贝，就算前的是空，后面也会被置为空
        BeanUtils.copyProperties(dto,courseBase);
//        设置机构id
        courseBase.setCompanyId(companyId);
//        设置创建时间
        courseBase.setCreateDate(LocalDateTime.now());
//        设置审核状态为未审核
        courseBase.setAuditStatus("202002");
//        设置发布状态为未发布
        courseBase.setAuditStatus("203001");
        int insert = courseBaseMapper.insert(courseBase);
        if(insert<=0){
            throw new RuntimeException("添加课程失败");
        }
        //像课程营销表写入数据
        CourseMarket courseMarket = new CourseMarket();
        Long id = courseBase.getId();
        BeanUtils.copyProperties(dto,courseMarket);
        courseMarket.setId(id);
        if(saveCourseMarket(courseMarket)<=0){
            throw new RuntimeException("添加课程失败");
        }
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(id);
        return courseBaseInfo;
    }
//    单独写一个查询两个表的信息的方法
public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
    //查询课程信息
    CourseBase courseBase = courseBaseMapper.selectById(courseId);
    if(courseBase == null){
        return null;
    }
    //查询营销信息
    CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
    //要返回的对象
    CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
    BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
    if(courseMarket != null){
        BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
    }
    //查询分类名称
    CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
    courseBaseInfoDto.setStName(courseCategoryBySt.getName());
    CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
    courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

    return courseBaseInfoDto;
}
//    单独写一个信息保存课程营销表
    private int saveCourseMarket(CourseMarket courseMarket){
//        参数的合法性校验
        String charge = courseMarket.getCharge();
        if(charge.equals("201001")){
            if(courseMarket.getPrice() == null || courseMarket.getPrice().floatValue()<=0){
                throw new RuntimeException("请输入合法的数据");
            }
        }
//        拿到id查询一下是否表中已经存在
        Long id = courseMarket.getId();
        CourseMarket courseMarket1 = courseMarketMapper.selectById(id);
        if(courseMarket1 == null){
            return courseMarketMapper.insert(courseMarket);
        }else{
            BeanUtils.copyProperties(courseMarket,courseMarket1);
            courseMarket1.setId(id);
            return courseMarketMapper.updateById(courseMarket1);
        }
    }
    @Transactional
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {

        //课程id
        Long courseId = dto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase==null){
            XueChengPlusException.cast("课程不存在");
        }

        //校验本机构只能修改本机构的课程
        if(!courseBase.getCompanyId().equals(companyId)){
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }

        //封装基本信息的数据
        BeanUtils.copyProperties(dto,courseBase);
        courseBase.setChangeDate(LocalDateTime.now());

        //更新课程基本信息
        int i = courseBaseMapper.updateById(courseBase);

        //封装营销信息的数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarket);
        saveCourseMarket(courseMarket);
        //查询课程信息
        CourseBaseInfoDto courseBaseInfo = this.getCourseBaseInfo(courseId);
        return courseBaseInfo;

    }
}
