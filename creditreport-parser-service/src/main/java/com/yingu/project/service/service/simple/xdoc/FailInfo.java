package com.yingu.project.service.service.simple.xdoc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 解析失败的信息记录
 * @Date: Created in 2018/4/18 13:41
 * @Author: wm
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FailInfo {
    String clazz;
    String property;
//    Integer index;
    Object value;
    String failMessage;
}
