package com.yingu.project.service.upload;

import com.aliyun.oss.OSSClient;

/**
 * @Date: Created in 2018/3/24 17:14
 * @Author: wm
 */
public class CreateOssClient {
    private static OSSClient ossclict = null;

    private CreateOssClient() throws Exception {
    }

    public static OSSClient getInstance() throws Exception {
        if (ossclict == null) {
            OssConnectConfig config = new OssConnectConfig();
            ossclict = new OSSClient(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
        }

        return ossclict;
    }
}
