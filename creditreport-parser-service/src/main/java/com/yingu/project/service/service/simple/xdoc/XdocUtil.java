package com.yingu.project.service.service.simple.xdoc;

import com.yingu.project.api.MatchInfo;
import com.yingu.project.api.SimilarInfo;
import com.yingu.project.util.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 字段解析工具类
 * 基本数据类型处理
 *
 * @Date: Created in 2018/4/2 13:54
 * @Author: wm
 */
public class XdocUtil {

    /**
     * 解析字符串最后面的int值
     *
     * @param str
     * @return
     */
    public static String analysisSuffixInteger(String str) {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        //去除后面的非数字
        while (!StringUtils.isNumeric(str.substring(str.length() - 1))) {
            str = str.substring(0, str.length() - 1);
            if (str.length() == 0) {
                return null;
            }
        }
        str = str.replaceAll(",", "").replaceAll("，", "");
        //取得最后面的数字
        String intStr = "";
        boolean flag = false;
        for (int j = str.length() - 1; j >= 0; j--) {
            Character itemChar = str.charAt(j);
            if (!StringUtils.isNumeric(itemChar.toString())) {
                break;
            }
            intStr = itemChar.toString().concat(intStr);
        }
        return intStr;
    }

    /**
     * 解析前面的int值
     *
     * @param str
     * @return
     */
    public static String analysisPrefixInteger(String str) {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        //去除前面的非数字字符
        while (!StringUtils.isNumeric(str.substring(0, 1))) {
            str = str.substring(1);
            if (str.length() == 0) {
                return null;
            }
        }
        str = str.replaceAll(",", "").replaceAll("，", "");
        //取得最前面的数字
        String intStr = "";
        for (int j = 0; j < str.length(); j++) {
            Character itemChar = str.charAt(j);
            if (!StringUtils.isNumeric(itemChar.toString())) {
                break;
            }
            intStr = intStr.concat(itemChar.toString());
        }
        return intStr;
    }

    /**
     * 解析前面的金额
     *
     * @param str
     * @return
     */
    public static DocMoney analysisPrefixMoney(String str) {
        DocMoney docMoney = new DocMoney();
        if (StringUtil.isEmpty(str)) {
            return docMoney;
        }
        //去除前面的非数字字符
        while (!StringUtils.isNumeric(str.substring(0, 1))) {
            str = str.substring(1);
            if (str.length() == 0) {
                return docMoney;
            }
        }
        str = str.replaceAll("，", ",");
        if (str.startsWith(",")) {
            return docMoney;
        }
        //取得最前面的数字
        String intStr = "";
        //有分号标示后面有3个数字
        int lastNum = 0;
        //默认只开启一次
        int lastNumFlag = 1;
        if (str.substring(0, 1).equals("0")) {
            lastNumFlag = 0;
        }
        for (int j = 0; j < str.length(); j++) {
            Character itemChar = str.charAt(j);
            if (!StringUtils.isNumeric(itemChar.toString())) {
                if (",".equals(itemChar.toString())) {
                    if (lastNum > 0) {
//                        for (int num=0;num<lastNum;num++){
//                            intStr = intStr.concat("0");
//                        }
                        break;
                    } else {
                        if (lastNumFlag == 0) {
                            break;
                        } else {
                            lastNum = 4;
                            lastNumFlag--;
                        }
                    }
                } else {
                    if (lastNum == 0) {
                        break;
                    }
                }
            }
            intStr = intStr.concat(itemChar.toString());
            if (lastNum > 0) {
                lastNum--;
            }
        }
        docMoney.setMoneyStr(intStr);
        if (!StringUtil.isEmpty(intStr)) {
            String tempStr = intStr.replaceAll(",", "");
            if (StringUtils.isNumeric(tempStr)) {
                BigDecimal money = new BigDecimal(tempStr);
                if (BigDecimal.ZERO.compareTo(money) == 0 && tempStr.length() > 1) {
                    docMoney.setMoney(null);
                } else {
                    docMoney.setMoney(money);
                }
            }
        }
        return docMoney;
    }

    /**
     * 字符串中最前面提取日期
     * 日期格式默认：yyyy年M月d日
     *
     * @param str
     * @return
     */
    public static DocDate analysisPrefixDate(String str) {
        return analysisPrefixDate(str, "yyyy年M月d日");
    }

    /**
     * 字符串中最前面提取日期
     *
     * @param str         前后不能有非日期数字
     * @param datePattern 注意：最后一位必须是非数字
     * @return
     */
    public static DocDate analysisPrefixDate(String str, String datePattern) {
        DocDate docDate = new DocDate();
        if (StringUtil.isEmpty(str)) {
            return docDate;
        }
        //去除前面的非数字字符
        while (!StringUtils.isNumeric(str.substring(0, 1))) {
            str = str.substring(1);
            if (str.length() == 0) {
                return null;
            }
        }
        //固定取最后一个为数字的字符
        while (!StringUtils.isNumeric(str.substring(str.length() - 1))) {
            str = str.substring(0, str.length() - 1);
        }
        str = str.concat(datePattern.substring(datePattern.length() - 1));
        String dateStr = str.replaceAll("\\.", "").replaceAll(" ", "");
        docDate.setDateStr(dateStr);
        if (dateStr.length() >= datePattern.length()) {
            Date date = XdocUtil.getDateValue(dateStr, datePattern);
            docDate.setDate(date);
        }
        return docDate;
    }
//    public static String analysisPrefixDate(String str, String datePattern) {
//        //固定取最后一个为数字的字符，作为日
//        while (!StringUtils.isNumeric(str.substring(str.length() - 1))) {
//            str = str.substring(0, str.length() - 1);
//        }
//        str = str.concat(datePattern.substring(datePattern.length() - 1));
//        String cardDistributionDateStr = str.replaceAll("\\.", "").replaceAll(" ", "");
//        return cardDistributionDateStr;
//    }

    /**
     * 解析在某个字符串之前的字符串
     *
     * @param source
     * @param close
     * @return 失败返回null
     */
    public static String substringBefore(String source, String close) {
        String result = StringUtils.substringBefore(source, close);
        if (result.length() < source.length()) {
//            result = result.substring(close.length());
            return result;
        }
        return null;
    }

    public static Integer getIntegerValue(Object s) {
        if (null == s) {
            return null;
        }
        try {
            return Integer.parseInt(s.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getDateValue(String dateStr, String pattern) {
        if (null == dateStr) {
            return null;
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 取得第一个不为null的值
     *
     * @param indexArr 必填
     * @return
     */
    public static Integer getFirstNotNullIndex(Integer... indexArr) {
        for (Integer index : indexArr) {
            if (null != index) {
                return index;
            }
        }
        return null;
    }


    /**
     * @param itemStr
     * @param separatorArr 依次取after的参考string，有值后return
     * @return 失败返回null
     * @date: Created in 2018/4/9 9:43
     * @author: wm
     */
    public static String substringAfter(String itemStr, String[] separatorArr) {
        if (null == separatorArr || separatorArr.length == 0) {
            return null;
        }
        String result = null;
        for (String separator : separatorArr) {
            result = StringUtils.substringAfter(itemStr, separator);
            if (!StringUtil.isEmpty(result)) {
                return result;
            }
        }
        return null;
    }

    /**
     * 依次取中间值，取到结果后立刻return
     *
     * @param str
     * @param startArr
     * @param endArr
     * @return
     */
    public static String substringBetween(String str, String[] startArr, String[] endArr) {
        String result = null;
        for (String start : startArr) {
            for (String end : endArr) {
                result = StringUtils.substringBetween(str, start, end);
                if (!StringUtil.isEmpty(result)) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * 模糊匹配 取尾部字符串
     * 当有多个结果则取第一个
     *
     * @param sourceStr
     * @param start
     * @return
     */
    public static String substringFuzzyAfter(String sourceStr, SimilarityKeyword start) {
        //开始索引
        Integer startIndex = null;
        MatchInfo startMatchInfo = fuzzySearchFirstMatch(sourceStr, start);
        if (null == startMatchInfo) {
            return null;
        }
        startIndex = startMatchInfo.getSourceStartIndex() + startMatchInfo.getMatchStr().length();
        if (null == startIndex) {
            return null;
        }

        //截取
        String result = StringUtils.substring(sourceStr, startIndex);
        return result;
    }

    /**
     * 模糊匹配 范围取值
     * 当有多个结果则取第一个
     *
     * @param sourceStr
     * @param start
     * @param end
     * @return
     */
    public static String substringFuzzyBetween(String sourceStr, SimilarityKeyword start, SimilarityKeyword end) {
        //开始索引
        Integer startIndex = null;
        MatchInfo startMatchInfo = fuzzySearchFirstMatch(sourceStr, start);
        if (startMatchInfo != null) {
            startIndex = startMatchInfo.getSourceStartIndex() + startMatchInfo.getSourceStr().length() - startMatchInfo.getTargetStartIndex();
        }
        //bug 可能尾部有未识别的字符
        if (null == startIndex) {
            return null;
        }

        //结束索引
        Integer endIndex = null;
        int maxRange = 2;
        if (end != null) {
            if (end.getScore() == 100) {
                maxRange = 0;
            } else if (end.getScore() > 90) {
                maxRange = 1;
            }
        }
        List<MatchInfo> matchInfoList = fuzzySearchMatchList(sourceStr, end, maxRange);
        if (null != matchInfoList) {
            for (MatchInfo matchInfo : matchInfoList) {
                //endIndex必须大于startIndex
                if (matchInfo.getSourceStartIndex() > startIndex + 1) {
                    endIndex = matchInfo.getSourceStartIndex();
                    break;
                }
            }
        }
        if (null == endIndex) {
            return null;
        }

        //截取
        String result = StringUtils.substring(sourceStr, startIndex, endIndex);
        return result;
    }

    public static List<MatchInfo> fuzzySearchMatchList(String sourceStr, SimilarityKeyword keyword, int maxRage) {
        if (StringUtil.isEmpty(sourceStr) || null == keyword) {
            return null;
        }

//        System.out.println(keyword.getKeyword() + "\t" + sourceStr);
        SimilarInfo similarInfoEnd = StringUtil.getSimilarDetailsInfo(sourceStr, keyword.getKeyword(), keyword.getScore(), maxRage);
        if (null == similarInfoEnd || Boolean.FALSE.equals(similarInfoEnd.getIsSimilar())) {
            return null;
        }
        List<MatchInfo> matchInfoList = similarInfoEnd.getMatchInfo();
        return matchInfoList;
    }

    public static MatchInfo fuzzySearchFirstMatch(String sourceStr, SimilarityKeyword keyword) {
        if (StringUtil.isEmpty(sourceStr) || null == keyword) {
            return null;
        }

        List<MatchInfo> matchInfoList = fuzzySearchMatchList(sourceStr, keyword, 2);

        if (null == matchInfoList || matchInfoList.size() == 0) {
            return null;
        }
        return matchInfoList.get(0);
    }

    public static boolean fuzzyContains(String sourceStr, SimilarityKeyword keyword) {
        if (StringUtil.isEmpty(sourceStr) || null == keyword) {
            return false;
        }

        List<MatchInfo> list = fuzzySearchMatchList(sourceStr, keyword, 2);

        if (null == list || list.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 相加求和
     *
     * @param num1
     * @param num2
     * @return
     */
    public static Integer intAdditive(Integer num1, Integer num2) {
        if (null == num1) {
            return num2;
        }
        if (null == num2) {
            return num1;
        }
        return num1 + num2;
    }

    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }
}
