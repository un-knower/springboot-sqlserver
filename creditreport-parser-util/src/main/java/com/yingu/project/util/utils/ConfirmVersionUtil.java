package com.yingu.project.util.utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.util.List;

/**
 * 确认版本
 * 0-明细，1-2011版，2-2012版
 */
public class ConfirmVersionUtil {
    private int result = -1;
    private List<XWPFParagraph> paras;

    public ConfirmVersionUtil(XWPFDocument xdoc) {
        paras = xdoc.getParagraphs();
    }

    /**
     * 明细-----------------------------
     * 明细）
     * 一个人基本信息 =
     * 二信息概要
     * 编制说明 =
     * 账户状态 =
     * 2011-----------------------------
     * 11银
     * 一个人基本信息 =
     * 二信息概要
     * 报告说明 =
     * 共享额度 =
     * 2012-----------------------------
     * 12银
     * 个人基本信息 =
     * 信息概要
     * 报告说明 =
     * 共享额度 =
     */

    private void subhead() {
        if (paragraphContains("明细）"))
            result = 0;
        else if (paragraphContains("1银"))
            result = 1;
        else if (paragraphContains("2银"))
            result = 2;
    }

    private void paragraphHeading() {
        if (paragraphContains("编制说明")) {
            result = 0;
        } else if (paragraphContains("报告说明")) {
            if (paragraphContains("一个人基本信息", "一") || paragraphContains("二信息概要", "二"))
                result = 1;
            else
                result = 2;
        }
    }

    /**
     * 0-明细，1-2011版，2-2012版
     *
     * @return
     */
    public int confirm() {
        subhead();
        if (result != -1)
            return result;
        paragraphHeading();
        if (result != -1)
            return result;

        return 9;
    }

    private boolean paragraphContains(String keyword) {
        for (XWPFParagraph para : paras) {
            String paraText = TableUtil.text(para);
            if (null != paraText && paraText.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean paragraphContains(String keyword, String mastHasChar) {
        for (XWPFParagraph para : paras) {
            String paraText = TableUtil.text(para);
            if (null != paraText && StringUtil.isSimilarIgnoreCount(keyword, paraText, mastHasChar)) {
                return true;
            }
        }
        return false;
    }
}
