package com.yingu.project.api;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by MMM on 2018/02/27.
 * 分页查询结果参数
 */
@Getter
@Setter
public class ResponsePagerResult<T> extends ResponseResult{
    public Long rowCount;
}
