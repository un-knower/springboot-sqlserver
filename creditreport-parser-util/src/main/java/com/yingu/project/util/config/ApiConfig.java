package com.yingu.project.util.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by MMM on 2017/08/30.
 * api接口配置
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "api-config")
public class ApiConfig {
    public String nonebank_url;
    public String nonebank_access_key;
    public String nonebank_secret_key;
    /**
     * 个人征信路径
     */
    public String creditword_path;
    /* 解析成功路径 */
    public String creditword_sucess_path;
    /* 解析失败路径 */
    public String creditword_fail_path;

    public String creditpdf_source;
    public String creditpdf_success;
    public String creditpdf_fail;

    public String abbyy_dll_folder;
    public String abbyy_serial_number;

    /* 扫描word为空,延时时间 */
    public Integer scan_delay_time;
    /* 是否开启扫描word文件夹 */
    public Boolean open_analyse_word;
    /* 是否保存重复的案件编号文档 */
    public Boolean save_repeat_report;

}
