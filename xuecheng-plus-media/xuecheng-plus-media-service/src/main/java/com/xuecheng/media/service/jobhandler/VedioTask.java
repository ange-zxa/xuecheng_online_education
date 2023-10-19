package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/9/13
 * \* Time: 11:39
 * \* Description:
 * \
 */
@Slf4j
public class VedioTask {
    @Autowired
    MediaFileProcessService mediaFileProcessService;
    @Autowired
    MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;

    @XxlJob("videoJobHandler")
    public void shardingJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        //确定cpu核心数量
        int i1 = Runtime.getRuntime().availableProcessors();
        //查询任务
        List<MediaProcess> mediaProcesses = mediaFileProcessService.selectListByShardIndex(shardIndex, shardTotal, i1);
        int size = mediaProcesses.size();
        if(size <=0){
            log.error("当前没有任务");
            return;
        }

        //开启任务
        ExecutorService executorService = Executors.newFixedThreadPool(i1);
        //使用的计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        //将任务加入线程池
        mediaProcesses.forEach(item ->{
            executorService.execute(()->{
                try {
                    Long taskId = item.getId();
                    boolean b = mediaFileProcessService.startTask(taskId);
                    if (!b) {
                        log.debug("抢占任务失败，任务id:{}", taskId);
                        return;
                    }
                    //拿到桶
                    String bucket = item.getBucket();
                    //拿到文件路径
                    String filePath = item.getFilePath();
                    //文件md5值 在这里也就是文件id
                    String fileId = item.getFileId();

                    //下载视频到本地
                    File file = mediaFileService.downloadFileFromMinIO(bucket, filePath);
                    if (file == null) {
                        log.error("下载视频出错，任务id：{},bucketId:{},objectName:{}", taskId, bucket, filePath);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "下载文件到本地失败");
                        return;
                    }
                    //源avi格式视频的路径
                    String video_path = file.getAbsolutePath();
                    //转化后的视频名称
                    String mp4_name = fileId + ".mp4";
                    //转换后MP4文件的路径
                    //先建立一个临时文件，作为转化后的文件
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("minio", "mp4");
                    } catch (IOException e) {
                        log.error("创建临时文件异常");
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "创建临时文件异常");
                        return;
                    }
                    String mp4_Path = mp4File.getAbsolutePath();
                    //创建工具类对象
                    Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpegpath, video_path, mp4_name, mp4_Path);
                    String s = mp4VideoUtil.generateMp4();
                    if (!s.equals("success")) {
                        //代表任务为失败
                        log.error("视频转码失败,bucket:{},objectName:{},原因：{}", bucket, filePath, s);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "视频转码失败");
                    } else {
                        //上传到minio
                        boolean b1 = mediaFileService.addMediaFilesToMinIO(mp4_Path, "video/mp4", bucket, filePath);
                        if (!b1) {
                            log.error("上传到minio失败,bucket:{},objectName:{},原因：{}", bucket, filePath, s);
                            mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "视频上传到minio失败");
                        }
                        //保存到库
                        String url = getFilePathByMd5(fileId, ".mp4");
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "2", fileId, url, "视频保存到库成功");
                    }
                }finally {
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await(30, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        });




        XxlJobHelper.log("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);

        // 业务逻辑
        for (int i = 0; i < shardTotal; i++) {
            if (i == shardIndex) {
                XxlJobHelper.log("第 {} 片, 命中分片开始处理", i);
            } else {
                XxlJobHelper.log("第 {} 片, 忽略", i);
            }
        }

    }
    private String getFilePathByMd5(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

}
