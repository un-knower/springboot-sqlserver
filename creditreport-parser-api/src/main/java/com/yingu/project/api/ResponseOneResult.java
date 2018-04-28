package com.yingu.project.api;

import lombok.*;

/**
 * Created by MMM on 2018/03/21.
 * 返回单个实体
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseOneResult<T> extends ResponseBase{
    public T data;
}
