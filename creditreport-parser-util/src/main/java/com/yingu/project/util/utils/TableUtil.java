package com.yingu.project.util.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TableUtil {

    /**
     * 获取某种类型的tables
     *
     * @param tables
     * @param key
     * @return
     */
    public static List<XWPFTable> getTables(String key, List<XWPFTable> tables) {
        List<XWPFTable> tablesClassified = new ArrayList<>();
        for (XWPFTable table : tables) {
            if (determineType(table, key))
                tablesClassified.add(table);
        }
        return tablesClassified;
    }

    public static List<XWPFTable> getTables(int cellSize, String key, List<XWPFTable> tables) {
        List<XWPFTable> tablesClassified = new ArrayList<>();
        for (XWPFTable table : tables) {
            List<XWPFTableRow> rows = table.getRows();
            if (rows.size() < 1)
                continue;
            List<XWPFTableCell> cells = rows.get(0).getTableCells();
            if (cells.size() != cellSize)
                continue;
            if (determineType(table, key)) {
                tablesClassified.add(table);
            } else {

            }
        }
        return tablesClassified;
    }

    public static List<BodyElement> doc2Elements(XWPFDocument xdoc) {
        List<BodyElement> elements = new ArrayList<>();
        List<IBodyElement> iElements = xdoc.getBodyElements();
        int j = 0;
        for (int i = 0; i < iElements.size(); i++) {
            IBodyElement iElement = iElements.get(i);
            BodyElement element = new BodyElement(i);
            if (iElement instanceof XWPFParagraph) {
                element.setElementType(BodyElementType.PARAGRAPH);
                element.setPara((XWPFParagraph) iElement);
                XWPFParagraph aa = (XWPFParagraph) iElement;
                aa.getCTP();
                aa.getPart();
                XWPFParagraph bb = new XWPFParagraph(aa.getCTP(), aa.getBody());
            } else if (iElement instanceof XWPFTable) {
                element.setElementType(BodyElementType.TABLE);
                element.setTable((XWPFTable) iElement);
                element.setIndexTable(j++);
            }
            elements.add(element);
        }
        return elements;
    }

    /**
     * 判断table的类型
     *
     * @param table
     * @param key
     * @return
     */
    public static boolean determineType(XWPFTable table, String key) {
        if (null == table || null == key) {
            return false;
        }
        List<XWPFTableRow> rows = table.getRows();
        if (rows == null || rows.size() == 0) {
            return false;
        }
        String cellValue;
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                cellValue = StringUtil.trim(cell.getText());
                if (key.equals(cellValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    //输出表格
    public static void outTable(List<XWPFTable> tables) {
        for (int i = 0; i < tables.size(); i++) {
            System.out.printf("---------------------------------表格%s\n", (i + 1));
            outTable(tables.get(i));
        }
    }

    public static void outTable(XWPFTable table) {
        List<XWPFTableRow> rows = table.getRows();
        List<XWPFTableCell> cells;
        XWPFTableRow row;
        XWPFTableCell cell;
        String cellText;
        for (int i = 0; i < rows.size(); i++) {
            System.out.printf("第%s行\t", i + 1);
            row = rows.get(i);
            cells = row.getTableCells();
            for (int j = 0; j < cells.size(); j++) {
                cell = cells.get(j);
                cellText = cell.getText();
                System.out.printf("%s\t", cellText);
            }
            System.out.print("\n");
        }
    }

    public static String typo(String str) {
        str = StringUtils.deleteWhitespace(str);
        str = str.replaceAll("\n", "");
        str = str.replaceAll("査", "查");
        str = str.replaceAll("曰", "日");
        str = str.replaceAll("情息", "信息");

        str = str.replaceAll("■", "");
        str = str.replaceAll("K常", "正常");
        str = str.replaceAll("芷常", "正常");
        return str;
    }

    /**
     * 小标题
     *
     * @param str
     * @return
     */
    public static String typoSubhead(String str) {
        str = StringUtils.deleteWhitespace(str);
        str = str.replaceAll("査", "查");//查询记录，査询记录
        str = str.replaceAll("情", "信");//信息，情息
        str = str.replaceAll("-", "一");//（一）贷款，（-）贷款
        str = str.replaceAll("＞", "）");//（二）贷记卡，（二＞贷记卡
        str = str.replaceAll("H", "三");//（三）准贷记卡，（H）准贷记卡
        return str;
    }

    /**
     * 概要
     *
     * @param str
     * @return
     */
    public static String typoOutline(String str) {
        str = str.replaceAll("吊", "币");//人民吊
        str = str.replaceAll("帀", "币");//人民帀
        str = str.replaceAll("亚", "业");//业务号——亚务号
        str = str.replaceAll("乃", "月");//按月归还——按乃归还
        str = str.replaceAll("茧", "至");//截至——截茧
        str = str.replaceAll("截M", "截至");
        str = str.replaceAll("枸", "构");//机构——机枸
        str = str.replaceAll("倮", "保");//担保——担倮
        return str;
    }

    public static String substringBetween(String str, List<String> begins, List<String> ends) {


//        if (str == null || open == null || close == null) {
//            return null;
//        }
//        final int start = str.indexOf(open);
//        if (start != INDEX_NOT_FOUND) {
//            final int end = str.indexOf(close, start + open.length());
//            if (end != INDEX_NOT_FOUND) {
//                return str.substring(start + open.length(), end);
//            }
//        }
        return null;
    }

    public static String text(XWPFTableCell cell) {
        return typo(cell.getText());
    }

    public static String text(XWPFParagraph para) {
        return typo(para.getText());
    }

    public static String textSubhead(XWPFParagraph para) {
        return typoSubhead(para.getText());
    }

    public static Boolean isSimilarCountEqual(XWPFTableCell cell, String strCompared) {
        return StringUtil.isSimilarCountEqual(text(cell), strCompared);
    }

    public static String outlineFormat(String outline) {
        //去除序号及序号符号。如果前3位有.，就把.及其之前的去掉
        String serialNumber = outline.substring(0, 3);
        String[] serialNumberSymbols = {".", "、", "*", ","};//index
        int indexSerialNumberSymbol = -1;
        for (int i = 0; i < serialNumberSymbols.length; i++) {
            int temp = serialNumber.indexOf(serialNumberSymbols[i]);
            if (temp != -1) {
                indexSerialNumberSymbol = indexSerialNumberSymbol > temp ? indexSerialNumberSymbol : temp;
            }
        }
        if (indexSerialNumberSymbol > -1) {
            outline = outline.substring(indexSerialNumberSymbol + 1, outline.length());
        }
        //替换错别字
        outline = typoOutline(outline);
        //将,(英文逗号)替换为，(中文逗号)
        outline = typoCommaSymbol_outline(outline);
        return outline;
    }

    /**
     * 将,(英文逗号)替换为，(中文逗号)
     * 2,300,000元
     * 前一位，后三位
     *
     * @param str
     * @return
     */
    public static String typoCommaSymbol_outline(String str) {
        char[] arrChar = str.toCharArray();
        for (int i = 0; i < arrChar.length; i++) {
            char cc = arrChar[i];
            if (cc == ',') {
                if (i > -1 && i + 3 < arrChar.length) {
                    String before = str.substring(i - 1, i);
                    String after = str.substring(i + 1, i + 4);
                    if (!StringUtils.isNumeric(before) || !StringUtils.isNumeric(after)) {
                        arrChar[i] = '，';
                    }
                }
            }
        }
        return String.valueOf(arrChar);
    }
}
