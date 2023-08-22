package com.xuecheng.media;

import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.junit.jupiter.api.Test;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: 祝先澳
 * \* Date: 2023/8/6
 * \* Time: 21:40
 * \* Description:
 * \
 */
public class MinioTest {

    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    //上传文件
    @Test
    public  void upload() {
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")
//                    .object("test001.mp4")
                    .object("001/test001.mp4")//添加子目录
                    .filename(""    )
                    .contentType("video/mp4")//默认根据扩展名确定文件内容类型，也可以指定
                    .build();
            minioClient.uploadObject(testbucket);
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }

    }

}
