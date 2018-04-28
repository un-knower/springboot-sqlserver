package com.yingu.project.service.service.simple.xdoc;

import org.apache.commons.lang3.StringUtils;

/**
 * 用于实现是否属于一行数据的判断
 *
 * @Date: Created in 2018/4/4 18:03
 * @Author: wm
 */
public interface XdocParagraphFillter {

    /**
     * 是否是相同显示块
     *
     * @param currentLine
     * @return default true
     */
    public default boolean isSameBlock(String currentLine) {
        return true;
    }

    /**
     * 是否属于同一行
     *
     * @param prevLine    前一行
     * @param currentLine 当前行
     * @return 是否为一条数据 default false
     */
    public default boolean isSameLine(String prevLine, String currentLine) {return false;}
}
