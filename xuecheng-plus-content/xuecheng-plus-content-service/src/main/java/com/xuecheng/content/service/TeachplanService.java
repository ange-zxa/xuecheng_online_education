package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/8/3
 * \* Time: 11:36
 * \* Description:
 * \
 */
public interface TeachplanService {
    List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * 新增、修改、保存章节信息
     * @param teachplanDto
     */
    void saveTeachplan(SaveTeachplanDto teachplanDto);
    void deleteTeachplan(Long teachplanId);
}
