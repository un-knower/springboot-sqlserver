package com.yingu.project.api.NoneBankAntifraud;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by MMM on 2018/01/30.
 * 黑名单查询参数
 */
@Getter
@Setter
public class BlackListParam {
    /** 进件编号 */
    private String intoinfono;
    private String name;
    private String idcard;
    private String phone;
}
