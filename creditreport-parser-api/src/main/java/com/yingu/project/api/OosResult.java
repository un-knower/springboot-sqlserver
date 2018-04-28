package com.yingu.project.api;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by MMM on 2018/04/02.
 * 阿里云文件操作返回结果
 */
@Getter
@Setter
public class OosResult {
    /* 是否成功 */
    public Boolean isSuccess;
    /* 文件路径 */
    public String filePath;
}
