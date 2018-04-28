package com.yingu.project.util.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by MMM on 2018/04/02.
 * 阿里云文件存储配置
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oss-config")
public class OssConfig {
    public String endpoint;
    public String accesskeyid;
    public String accesskeysecret;
    public String bucketname;
    public String viewfile;
    public Boolean isopen;
}
