package com.yingu.project.service.service.simple.xdoc;

import org.apache.commons.lang3.StringUtils;

//概要工具
public class OutlineUtil {
    //发卡日期
    public static String IssueDate(String outline) {
        String sss = null;
        if (outline.length() > 11) {
            sss = outline.substring(0, 11);
            DocDate docDate = XdocUtil.analysisPrefixDate(sss);
            sss = docDate.getDateStr();
        }
        return sss;
    }

    //发卡机构。固定"“"开头，“””结尾。机构“中国民生银行”发放
    //TODO 2012的构K VH ”
    public static String CardIssuer(String outline) {
        String sss = null;

        sss = outline.replaceAll("枸", "构");//机构——机枸
        String[] begin = {"机构", "构", "机"};
        sss = substringAfter(sss, begin);
        String[] end = {"发放", "发", "放"};
        sss = substringBefore(sss, end);

        sss = sss.replaceAll("《", "");//机构“——机构《
        sss = sss.replaceAll("》", "");
        sss = sss.replaceAll("«", "");
        sss = sss.replaceAll("»", "");
        sss = sss.replaceAll("\"", "");
        sss = sss.replaceAll("“", "");
        sss = sss.replaceAll("”", "");
        return sss;
    }

    //币种
    public static String Currency(String outline) {
        String sss = null;
        sss = StringUtils.substringBetween(outline, "（", "账户");
        return sss;
    }

    //业务号
    public static String BusinessNo(String outline) {
        String sss = null;
        sss = StringUtils.substringBetween(outline, "号", "，");
        return sss;
    }

    //额度。授信额度10000元、授信额度折合人民币10000元
    public static String LineOfCredit(String outline) {
        String sss = null;
        sss = StringUtils.substringBetween(outline, "度", "元");
        if (sss != null && sss.indexOf("币") > -1) {
            sss = StringUtils.substringAfter(sss, "币");
        }
        return sss;
    }

    //担保方式
    public static String GuarantyStyle(String outline) {
        String sss = null;
        int indexBao = outline.indexOf("保");
        if (indexBao != -1) {
            int commaBefore = outline.substring(0, indexBao).lastIndexOf("，");
            String[] end = {"。", "•", "，", "„", "^"};
            int commaAfter = outline.length();
            for (int i = 0; i < end.length; i++) {
                int commaAfterTemp = outline.indexOf(end[i], indexBao + 1);
                if (commaAfterTemp != -1) {
                    commaAfter = commaAfterTemp < commaAfter ? commaAfterTemp : commaAfter;
                }
            }
            sss = outline.substring(commaBefore + 1, commaAfter);
        }
        return sss;
    }

    public static String substringAfter(String str, String[] separators) {
        for (int i = 0; i < separators.length; i++) {
            String separator = separators[i];
            int index = str.indexOf(separator);
            if (index != -1) {
                str = str.substring(index + separator.length());
                return str;
            }
        }
        return str;
    }

    public static String substringBefore(String str, String[] separators) {
        for (int i = 0; i < separators.length; i++) {
            int index = str.indexOf(separators[i]);
            if (index != -1) {
                str = str.substring(0, index);
                return str;
            }
        }
        return str;
    }

    public static boolean contains(String string, String[] strs) {
        for (int i = 0; i < strs.length; i++) {
            int index = string.indexOf(strs[i]);
            if (index != -1) {
                return true;
            }
        }
        return false;
    }
}
