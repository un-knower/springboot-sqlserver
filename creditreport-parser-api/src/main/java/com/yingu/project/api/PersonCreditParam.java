package com.yingu.project.api;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.util.List;

/**
 * Created by MMM on 2018/03/28.
 * analyse参数实体
 */
@Getter
@Setter
public class PersonCreditParam {
    public XWPFTable table;
    /* 当前段落 */
    public XWPFParagraph para;
    /* 当前段落索引 */
    public Integer paraIndex;
    /* 所有段落 */
    List<XWPFParagraph> paras;
    /* 当前段落 */
    XWPFDocument xdoc;
    /* 文件名称 */
    String wordName;
    /* 所有表格 */
    public List<XWPFTable> tables;
    /* 当前表格索引 */
    public Integer tableIndex;

}
