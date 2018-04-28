package com.yingu.project.service.service.simple.xdoc;

import org.apache.commons.lang3.StringUtils;

/**
 * 用于实现是否属于一行数据的判断
 *
 * @Date: Created in 2018/4/4 18:03
 * @Author: wm
 */
public interface XdocCreditParagraphFillter extends XdocParagraphFillter {

    /**
     * 是否是相同显示块
     *
     * @param currentLine
     * @return default true
     */
    @Override
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
    @Override
    public default boolean isSameLine(String prevLine, String currentLine) {
        if ((currentLine.contains("到期")&&currentLine.indexOf("到期") < 30) || (currentLine.contains("截至")&&currentLine.indexOf("截至") < 30)) {
            return true;
        }
        //开头前2位若为数字且包含关键词“发放的”，则是新的一条记录
        if (currentLine.length()>5&&StringUtils.isNumeric(currentLine.substring(0, 2)) && (currentLine.contains("年") || currentLine.contains("月"))) {
            return false;
        }
        return true;
    }
}
