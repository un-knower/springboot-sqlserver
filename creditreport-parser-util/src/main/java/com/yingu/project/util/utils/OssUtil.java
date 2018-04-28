package com.yingu.project.util.utils;

import com.aliyun.oss.*;
import com.yingu.project.api.OosResult;
import com.yingu.project.util.config.OssConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.text.MessageFormat;

/**
 * Created by MMM on 2018/04/02.
 * 阿里云OSS存储工具
 */
@Slf4j
@Component
public class OssUtil {
//    private static String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
//    private static String accessKeyId = "LTAIOBwj156FRrAS";
//    private static String accessKeySecret = "l9TH1YPLglnVKgNctme9KsJ8w9k2Wx";

//    private static String bucketName = "*** Provide bucket name ***";
//    private static String key = "*** Provide key ***";

    public static OssUtil ossUtil;
    private static OSS ossClient = null;
    @Autowired
    OssConfig ossConfig;
    @PostConstruct
    public void init() {
        ossUtil = this;
    }


    /**
     * 获取OSSClient实例
     * @return
     */
    private static OSS getInstance(){
        //if(ossClient==null){
            ossClient=new OSSClientBuilder().build(ossUtil.ossConfig.getEndpoint(),
                    ossUtil.ossConfig.getAccesskeyid(),
                    ossUtil.ossConfig.getAccesskeysecret());
        //}
        return ossClient;
    }

    /**
     * 上传文件
     * @param inputStream
     * @param fileName 文件名称
     * @return
     */
    public static Boolean upload(InputStream inputStream,String fileName,String bucketName){
        boolean isSucess=true;
        try {
            ossClient=getInstance();
            ossClient.putObject(bucketName, fileName, inputStream);
        }
        catch (OSSException oe) {
            isSucess=false;
            log.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.error("Error Message: " + oe.getErrorCode());
            log.error("Error Code:       " + oe.getErrorCode());
            log.error("Request ID:      " + oe.getRequestId());
            log.error("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            isSucess=false;
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message: " + ce.getMessage());
        } finally {
            /*
             * Do not forget to shut down the client finally to release all allocated resources.
             */
            ossClient.shutdown();
        }
        return isSucess;
    }
}
