package com.yingu.project.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbbyyDto {
    String Name;//文件名
    byte[] bytes;
    /**
     * 200 OK
     * 400 请求参数有误
     * 500 OCR服务器异常
     */
    int code;//响应状态码
    String message;//响应提示
}
