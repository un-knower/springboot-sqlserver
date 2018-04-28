package com.yingu.project.service.upload;

import lombok.Getter;
import lombok.Setter;

/**
 * @Date: Created in 2018/3/24 17:10
 * @Author: wm
 */
@Getter
@Setter
public class OssConnectConfig {
    private String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
    private String accessKeyId="LTAIOBwj156FRrAS";
    private String accessKeySecret="l9TH1YPLglnVKgNctme9KsJ8w9k2Wx";
    private String bucketName="ygzxjk-qz-test";
//    private String downloadFile;
//    private int taskNum;
//    private long partSize;
//    private boolean enableCheckpoint;
    private String viedFile="http://ygzxjk-qz-test.oss-cn-shanghai.aliyuncs.com/";
//    private long expiration;
}
