package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileProcessService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/9/12
 * \* Time: 17:18
 * \* Description:mediaProcessservice的实现
 * \
 */
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    @Autowired
    MediaProcessMapper mediaProcessMapper;
    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;
    @Override
    public List<MediaProcess> selectListByShardIndex(int shardTotal, int shardIndex, int count) {
        List<MediaProcess> mediaProcesses = mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
        return mediaProcesses;
    }

    @Override
    public boolean startTask(long id) {
        int result = mediaProcessMapper.startTask(id);
        return result<=0?false:true;
    }

    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        //如果查询为空
        if(mediaProcess == null){
            return;
        }
        //如果任务执行失败
        //todo:校验这个方法写的对不对
        if(status.equals("3")){
            //更新表中状态
            LambdaUpdateWrapper<MediaProcess> mediaProcessLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            LambdaUpdateWrapper<MediaProcess> set = mediaProcessLambdaUpdateWrapper.eq(MediaProcess::getId, taskId).set(MediaProcess::getFailCount, mediaProcess.getFailCount() + 1)
                    .set(MediaProcess::getErrormsg, errorMsg);
            mediaProcessMapper.update(null,set);
            return;
        }
        //如果任务执行成功
        //更新media_file表中内容
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        mediaFiles.setUrl(url);
        //更新url
        mediaFilesMapper.updateById(mediaFiles);
        //更新media_process表的内容
        mediaProcess.setStatus("2");
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcess.setUrl(url);
        mediaProcessMapper.updateById(mediaProcess);
        //将media_process表中的信息插入到mediaprocessHistory表中
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess,mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        //从media_process表中删除记录
        mediaProcessMapper.deleteById(mediaProcess.getId());
    }
}
