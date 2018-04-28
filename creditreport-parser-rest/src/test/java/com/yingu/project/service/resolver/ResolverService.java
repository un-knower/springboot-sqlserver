package com.yingu.project.service.resolver;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date: Created in 2018/3/24 17:23
 * @Author: wm
 */
@SpringBootTest
@Slf4j
public class ResolverService {


    @Test
    public void resolver() {
        //解析接口地址
        String url = "http://172.24.132.121:9080/parse";
        //进件编号
        String intoAppId = "BJPHYB402018032300001";
        //oss文件访问地址
//        String fileUrl = "http://ygzxjk-qz-test.oss-cn-shanghai.aliyuncs.com/dd16b302-2140-4a2a-b443-1f14d869b566.pdf";
//        String fileKey = "dd16b302-2140-4a2a-b443-1f14d869b566";

//        http://ygzxjk-qz-test.oss-cn-shanghai.aliyuncs.com/068d9cf1-08f5-481f-8742-410af8e05bc9.pdf
        String fileKey = "068d9cf1-08f5-481f-8742-410af8e05bc9";

        String allUrl = url + "?orderNum=" + intoAppId + "&fileKey=" + fileKey;
        log.info("url地址为：" + allUrl);
        //调用解析文件接口
        String json = HttpUtil.sendGet(allUrl);

        System.out.println("/n/n>>>>>>>>>>>>>>>>");
        System.out.println(json);
        //{"code":"0003","msg":"解析不到结果","content":[],"summary":null}
        System.out.println("/n/n>>>>>>>>>>>>>>>>");

    }

}
