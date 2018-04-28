package com.yingu.project.service.upload;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.yingu.project.util.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.UUID;

/**
 * 阿里oss上传pdf简版报告文件
 * 依赖：
 * <dependency>
 * <groupId>com.aliyun.oss</groupId>
 * <artifactId>aliyun-sdk-oss</artifactId>
 * <version>2.3.0</version>
 * </dependency>
 *
 * @Date: Created in 2018/3/24 10:13
 * @Author: wm
 */
@SpringBootTest
@Slf4j
public class UploadServiceTest {
    @Test
    public void upload() {
//        String filePath = "D:/word/信报样本/wm-正常准贷记卡.pdf";
        //http://ygzxjk-qz-test.oss-cn-shanghai.aliyuncs.com/dd16b302-2140-4a2a-b443-1f14d869b566.pdf

        String filePath = "D:/word/wm-normal.pdf";


        String returnUrl = this.upload(filePath);
        System.out.println("\n\n>>>>>>>>>>>>>>>>>>\n" + returnUrl + "\n\n");
    }

    public String upload(String filePath) {
        String extName = "";
        String suffix = "";
        String webFilePath = "";
        String showUrl = "";
        //获取文件
        File file = new File(filePath);
        String fileName = file.getName();
        //文件重命名
        if (fileName.lastIndexOf(".") >= 0) {
            extName = fileName.substring(fileName.lastIndexOf("."));
            suffix = extName.replace(".", "");
        }
        String imagExt = suffix;

        System.out.println(imagExt);
        byte[] bytes = FileUtil.fileToByte(file);
        String returnUrl = this.upload(bytes, imagExt);
        return returnUrl;
    }

    private String upload(byte[] file_buff, String fileExt) {
        String filename = UUID.randomUUID() + "." + fileExt;
        OssConnectConfig ossConnectConfig = new OssConnectConfig();
        String remotePath = ossConnectConfig.getViedFile() + filename;
        OSSClient ossClient = null;

        try {
            ossClient = CreateOssClient.getInstance();
            ossClient.putObject(ossConnectConfig.getBucketName(), filename, new ByteArrayInputStream(file_buff));
            log.info("★[上传阿里云文件成功]★★[生成路径][" + remotePath + "]");
        } catch (OSSException var7) {
            log.error("Error Message: 上传失败，获取不到OSSClient连接请求" + var7.getErrorCode());
        } catch (Exception var8) {
            log.error("上传到阿里云附件失败");
            var8.printStackTrace();
        }

        return remotePath;
    }
}
