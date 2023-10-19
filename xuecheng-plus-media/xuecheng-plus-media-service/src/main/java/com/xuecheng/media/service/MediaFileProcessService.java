package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/9/12
 * \* Time: 17:17
 * \* Description:任务处理
 * \
 */
public interface MediaFileProcessService {
    List<MediaProcess> selectListByShardIndex( int shardTotal,  int shardIndex,  int count);
    public boolean startTask(long id);
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);
}
