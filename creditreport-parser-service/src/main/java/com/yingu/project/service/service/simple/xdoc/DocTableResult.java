package com.yingu.project.service.service.simple.xdoc;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Date: Created in 2018/4/19 10:58
 * @Author: wm
 */
@Getter
@Setter
public class DocTableResult<T> {
    /**是否组合表格*/
    Boolean isCombined = false;
    List<T> list;
}
