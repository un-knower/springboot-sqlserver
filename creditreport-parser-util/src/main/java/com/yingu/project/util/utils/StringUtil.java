package com.yingu.project.util.utils;

import com.yingu.project.api.MatchInfo;
import com.yingu.project.api.SimilarInfo;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by user on 2017/3/10.
 */
public class StringUtil {
    public static int size(String str) {
        if (isEmpty(str))
            return 0;
        else
            return str.length();
    }

    public static boolean isEmpty(String value) {
        if (value == null || value.trim().length() == 0)
            return true;
        else
            return false;
    }

    public static String format(String value) {
        return format(value, "");
    }

    public static String format(String value, String defaultValue) {
        if (isEmpty(value))
            return defaultValue;
        else
            return value.trim();
    }

    public static Long parseLong(String recordCount) {
        try {
            long l = Long.parseLong(recordCount);
            return l;
        } catch (Exception e) {
            return 0L;
        }
    }

    public static String ObjToString(Object obj) {
        return (obj == null) ? "" : obj.toString();
    }

    public static String parseJsonString(String input) {
        String output = input;
        if (output.startsWith("\""))
            output = output.substring(1);
        output = StringUtils.trimLeadingCharacter(output, '\"');
        output = StringUtils.trimTrailingCharacter(output, '\"');
        output = output.replace("\\\"", "\"");
        output = output.replace("\"{", "{");
        output = output.replace("}\"", "}");
        return output;
    }

    public static String parseJsonStringV2(String input) {
        String output = input;
        if (output.startsWith("\""))
            output = output.substring(1);
        output = StringUtils.trimLeadingCharacter(output, '\"');
        output = StringUtils.trimTrailingCharacter(output, '\"');
        output = output.replace("\\\"", "\"");
        output = output.replace("\"{", "{");
        output = output.replace("}\"", "}");
        output = output.replace("\"[", "[");
        output = output.replace("]\"", "]");
        return output;
    }

    public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            return buf.toString();
            // 16位的加密
            //return buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String trim(String str) {
        //return str.replace(" ", "");
        return str.replaceAll("\\s*", "")
                .replace("''", "")
                .replace("査询", "查询");
    }

    public static String trimToNum(String str) {
        return trim(str).replace(",", "")
                .replace(".", "")
                .replace("I", "1");
    }

    public static String trimToDate(String str) {
        String strResult =
                str.replace(",", "-")
                        .replace(".", "-")
                        .replace("_", "-")
                        .replace("。", "-");
        return strResult;
    }

    public static void main(String[] args) {
        String strSource = "从未 1［期过的贷记卡及透支未趨过 60 夭的准贷记卡账户明細如下：";
        String strCompared = "从未逾期过的贷记卡及透支未超过60天的准贷记卡账户明细如下";
        boolean iss = isSimilar(strSource, strCompared, 7);
        System.out.println(iss);
    }

    /**
     * 判断两个字符串相似度,可设置level
     *
     * @param strSource   原字符串(标准字符串,被除数)
     * @param strCompared 比较字符串
     * @param level       评分阀值
     * @param moreCount   比较字符串比原字符串多多少个限制
     * @return
     */
    public static Boolean isSimilar(String strSource, String strCompared, int level, int moreCount) {
        if (strCompared.length() - strSource.length() > moreCount) {
            return false;
        }
        int count = strSource.length();
        int maxSameCount = 0;
        //遍历count次
        for (int i = 0; i < count; i++) {
            int nowSameCount = 0;
            int c = 0;
            int lastIndex = 0;//记录上一次匹配的目标索引
            //遍历每一次的原字符串所有字段
            for (int j = i; j < strSource.length(); j++) {
                char charSource = strSource.charAt(j);
                for (; c < strCompared.length(); c++) {
                    char charCompare = strCompared.charAt(c);
                    if (charSource == charCompare) {
                        nowSameCount++;
                        lastIndex = ++c;//如果匹配,手动加1
                        break;
                    }
                }
                c = lastIndex;//遍历完目标字符串,记录当前匹配索引
            }
            if (nowSameCount > maxSameCount) {
                maxSameCount = nowSameCount;
            }
        }
        //大于原字符串数量的情况
        if (maxSameCount > count) {
            // 10 , 7
            maxSameCount = count - (maxSameCount - count);
        }
        double dLv = (double) 100 * maxSameCount / count;
        int iLv = 10 * maxSameCount / count * 10;
        int cha = (int) dLv - iLv;
        int yu = cha > 5 ? 1 : 0;
        iLv += yu * 10;
        if (iLv / 10 >= level) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 判断两个字符串相似度,可设置level
     *
     * @param strSource   (标准字符串,被除数)
     * @param strCompared
     * @param level       1<level<10,10表示完全相同,1表示相似度10%
     * @return
     */
    public static Boolean isSimilar(String strSource, String strCompared, int level) {
        return isSimilar(strSource, strCompared, level, 2);
    }

    /**
     * 判断两个字符串相似度,level=6
     *
     * @param strSource   (标准字符串,被除数)
     * @param strCompared
     * @return
     */
    public static Boolean isSimilar(String strSource, String strCompared) {
        int level = 6;
        return isSimilar(strSource, strCompared, level);
    }

    /**
     * 判断两个字符串相似度
     *
     * @param strSource   (标准字符串,被除数)
     * @param strCompared
     * @param mastHasChar 必须包含其中一个字符
     * @return
     */
    public static Boolean isSimilar(String strSource, String strCompared, String mastHasChar) {
        boolean isHas = false;
        if (!isEmpty(mastHasChar)) {
            for (int i = 0; i < mastHasChar.length(); i++) {
                if (strCompared.indexOf(mastHasChar.charAt(i)) != -1) {
                    isHas = true;
                }
            }
            if (isHas == false) {
                return false;
            }
        }
        return isSimilar(strSource, strCompared);
    }

    /**
     * 判断两个字符串相似度
     *
     * @param strSource   (标准字符串,被除数)
     * @param strCompared
     * @param mastHasChar
     * @param mastNotHas  必须不包含的
     * @return
     */
    public static Boolean isSimilar(String strSource, String strCompared, String mastHasChar, String mastNotHas) {
        boolean isHas = false;
        if (!isEmpty(mastNotHas)) {
            for (int i = 0; i < mastNotHas.length(); i++) {
                if (strCompared.indexOf(mastNotHas.charAt(i)) != -1) {
                    isHas = true;
                }
            }
            if (isHas == true) {
                return false;
            }
        }
        return isSimilar(strSource, strCompared, mastHasChar);
    }

    /**
     * 获取无横线的UUID
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static Boolean isSimilarCountEqual(String strSource, String strCompared) {
        if (strCompared.length() != strSource.length()) {
            return false;
        }
        int level = 6;
        int count = strSource.length();
        int maxSameCount = 0;
        //遍历count次
        for (int i = 0; i < count; i++) {
            int nowSameCount = 0;
            int c = 0;
            int lastIndex = 0;//记录上一次匹配的目标索引
            //遍历每一次的原字符串所有字段
            for (int j = i; j < strSource.length(); j++) {
                char charSource = strSource.charAt(j);
                for (; c < strCompared.length(); c++) {
                    char charCompare = strCompared.charAt(c);
                    if (charSource == charCompare) {
                        nowSameCount++;
                        lastIndex = ++c;//如果匹配,手动加1
                        break;
                    }
                }
                c = lastIndex;//遍历完目标字符串,记录当前匹配索引
            }
            if (nowSameCount > maxSameCount) {
                maxSameCount = nowSameCount;
            }
        }
        //大于原字符串数量的情况
        if (maxSameCount > count) {
            // 10 , 7
            maxSameCount = count - (maxSameCount - count);
        }
        double dLv = (double) 100 * maxSameCount / count;
        int iLv = 10 * maxSameCount / count * 10;
        int cha = (int) dLv - iLv;
        int yu = cha > 5 ? 1 : 0;
        iLv += yu * 10;
        if (iLv / 10 >= level) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean isSimilarIgnoreCount(String strSource, String strCompared, String mastHasChar) {
        boolean isHas = false;
        if (!isEmpty(mastHasChar)) {
            for (int i = 0; i < mastHasChar.length(); i++) {
                if (strCompared.indexOf(mastHasChar.charAt(i)) != -1) {
                    isHas = true;
                }
            }
            if (isHas == false) {
                return false;
            }
        }

        int level = 6;
        int count = strSource.length();
        int maxSameCount = 0;
        //遍历count次
        for (int i = 0; i < count; i++) {
            int nowSameCount = 0;
            int c = 0;
            int lastIndex = 0;//记录上一次匹配的目标索引
            //遍历每一次的原字符串所有字段
            for (int j = i; j < strSource.length(); j++) {
                char charSource = strSource.charAt(j);
                for (; c < strCompared.length(); c++) {
                    char charCompare = strCompared.charAt(c);
                    if (charSource == charCompare) {
                        nowSameCount++;
                        lastIndex = ++c;//如果匹配,手动加1
                        break;
                    }
                }
                c = lastIndex;//遍历完目标字符串,记录当前匹配索引
            }
            if (nowSameCount > maxSameCount) {
                maxSameCount = nowSameCount;
            }
        }
        //大于原字符串数量的情况
        if (maxSameCount > count) {
            // 10 , 7
            maxSameCount = count - (maxSameCount - count);
        }
        double dLv = (double) 100 * maxSameCount / count;
        int iLv = 10 * maxSameCount / count * 10;
        int cha = (int) dLv - iLv;
        int yu = cha > 5 ? 1 : 0;
        iLv += yu * 10;
        if (iLv / 10 >= level) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从段落中查找与目标关键词最相似的关键词,并返回匹配到的关键词信息,可设置level
     *
     * @param strSource       原字符串
     * @param strCompared     比较字符串(标准字符串,被除数)
     * @param level           评分阀值
     * @param matchRangeIndex 比较字符串比原字符串字符位置多多少个限制
     * @return
     */
    public static SimilarInfo getSimilarInfo(String strSource, String strCompared, int level, int matchRangeIndex) {
        SimilarInfo similarInfo = new SimilarInfo();
        similarInfo.setStrCompared(strCompared);
        similarInfo.setStrSource(strSource);
        HashMap<Integer, List<String>> mapMatches = new HashMap<>();

        int count = strSource.length();
        int targetCount = strCompared.length();
        int maxSameCount = 0;
        //遍历count次
        for (int i = 0; i < count; i++) {
            String strMatches = "";
            int nowSameCount = 0;
            int c = 0;
            int lastIndex = 0;//记录上一次匹配的目标索引
            int lastSrcIndex = 0;//记录上一次匹配的源字符串索引
            //遍历每一次的原字符串所有字段
            for (int j = i; j < strSource.length(); j++) {
                if (nowSameCount > 0) {
                    if (j > lastSrcIndex + 1 + matchRangeIndex) {
                        break;
                    }
                }
                char charSource = strSource.charAt(j);
                for (; c < strCompared.length(); c++) {
                    char charCompare = strCompared.charAt(c);
                    if (charSource == charCompare) {
                        strMatches += charCompare;
                        nowSameCount++;
                        lastIndex = ++c;//如果匹配,手动加1
                        lastSrcIndex = j;
                        break;
                    }
                }
                c = lastIndex;//遍历完目标字符串,记录当前匹配索引
            }
            if (nowSameCount > maxSameCount) {
                maxSameCount = nowSameCount;
            }
            if (!isEmpty(strMatches)) {
                List<String> list;
                if (mapMatches.containsKey(nowSameCount)) {
                    list = mapMatches.get(nowSameCount);
                } else {
                    list = new ArrayList<>();
                }
                if (!list.contains(strMatches)) {
                    list.add(strMatches);
                }
                mapMatches.put(nowSameCount, list);
            }
        }
        if (mapMatches.containsKey(maxSameCount)) {
            similarInfo.setMatchList(mapMatches.get(maxSameCount));
        }
        double dLv = (double) 100 * maxSameCount / targetCount;
        int realLevel = (int) Math.rint(dLv);
        Boolean isSimilar;
        if (realLevel >= level) {
            isSimilar = true;
        } else {
            isSimilar = false;
        }
        similarInfo.setRealScore(realLevel);
        similarInfo.setIsSimilar(isSimilar);
        return similarInfo;
    }

    /**
     * 从段落中查找与目标关键词最相似的关键词,并返回匹配到的关键词信息,可设置level
     *
     * @param strSource       原字符串
     * @param strCompared     比较字符串(标准字符串,被除数)
     * @param level           评分阀值
     * @param matchRangeIndex 比较字符串比原字符串字符位置多多少个限制
     * @return
     */
    public static SimilarInfo getSimilarDetailsInfo(String strSource, String strCompared, int level, int matchRangeIndex) {
        SimilarInfo similarInfo = new SimilarInfo();
        similarInfo.setStrCompared(strCompared);
        similarInfo.setStrSource(strSource);
        HashMap<Integer, List<MatchInfo>> mapMatches = new HashMap<>();
        int count = strSource.length();
        int targetCount = strCompared.length();
        int maxSameCount = 0;
        //遍历count次
        for (int i = 0; i < count; i++) {
            String strMatches = "";
            int nowSameCount = 0;
            int c = 0;
            int lastIndex = 0;//记录上一次匹配的目标索引
            int lastSrcIndex = 0;//记录上一次匹配的源字符串索引
            int firstTargetIndex = 0;
            int firstSrcIndex = 0;
            int targetStartIndex = 0;
            int targetEndIndex = 0;
            int sourceStartIndex = 0;
            int sourceEndIndex = 0;
            Boolean isFirst = true;
            //遍历每一次的原字符串所有字段
            for (int j = i; j < strSource.length(); j++) {
                if (nowSameCount > 0) {
                    if (j > lastSrcIndex + 1 + matchRangeIndex) {
                        break;
                    }
                }
                char charSource = strSource.charAt(j);
                for (; c < strCompared.length(); c++) {
                    char charCompare = strCompared.charAt(c);
                    if (charSource == charCompare) {
                        if (isFirst == true) {
                            isFirst = false;
                            firstTargetIndex = c;
                            firstSrcIndex = j;
                            //记录第一个匹配的索引
                            targetStartIndex = c;
                            sourceStartIndex = j;
                        }
                        //记录最后一个匹配的索引
                        targetEndIndex = c;
                        sourceEndIndex = j;

                        lastSrcIndex = j;
                        strMatches += charCompare;
                        nowSameCount++;
                        lastIndex = ++c;//如果匹配,手动加1
                        break;
                    }
                }
                c = lastIndex;//遍历完目标字符串,记录当前匹配索引
            }
            if (nowSameCount > maxSameCount) {
                maxSameCount = nowSameCount;
            }
//            if (!isEmpty(strMatches)) {
            if(null!=strMatches&&strMatches.length()>0){
                List<MatchInfo> list;
                if (mapMatches.containsKey(nowSameCount)) {
                    list = mapMatches.get(nowSameCount);
                } else {
                    list = new ArrayList<>();
                }
                MatchInfo matchInfo = new MatchInfo();
                matchInfo.setMatchStr(strMatches);
//				matchInfo.setSourceIndex(firstSrcIndex);
//				matchInfo.setTargetIndex(firstTargetIndex);
                matchInfo.setTargetStartIndex(targetStartIndex);
                matchInfo.setTargetEndIndex(targetEndIndex);
                matchInfo.setSourceStartIndex(sourceStartIndex);
                matchInfo.setSourceEndIndex(sourceEndIndex);
                Boolean isHas = false;
                for (MatchInfo item : list) {
                    if (item.getMatchStr().equals(matchInfo.getMatchStr())
                            && item.getTargetStartIndex().equals(matchInfo.getTargetStartIndex())
                            && item.getTargetEndIndex().equals(matchInfo.getTargetEndIndex())
                            && item.getSourceStartIndex().equals(matchInfo.getSourceStartIndex())
                            && item.getSourceEndIndex().equals(matchInfo.getSourceEndIndex())) {
                        isHas = true;
                    }
                }
                if (!isHas) {
                    list.add(matchInfo);
                    mapMatches.put(nowSameCount, list);
                }
            }
        }
        if (mapMatches.containsKey(maxSameCount)) {
            List<MatchInfo> matchInfoList = mapMatches.get(maxSameCount);
            for (MatchInfo item : matchInfoList) {
                int srcStartIndex = 0;
                int srcEndIndex = 0;
                if (item.getTargetStartIndex() == 0) {
                    srcStartIndex = item.getSourceStartIndex();
                } else {
                    srcStartIndex = item.getSourceStartIndex() - item.getTargetStartIndex();
                }
                if (item.getTargetEndIndex() == targetCount - 1) {
                    srcEndIndex = item.getSourceEndIndex();
                } else {
                    srcEndIndex = item.getSourceEndIndex() + (targetCount - 1 - item.getTargetEndIndex());
                }
                srcStartIndex = srcStartIndex < 0 ? 0 : srcStartIndex;
                srcEndIndex = srcEndIndex > count - 1 ? count - 1 : srcEndIndex;
                String sourceStr = strSource.substring(srcStartIndex, srcEndIndex + 1);
                item.setSourceStr(sourceStr);
            }
            similarInfo.setMatchInfo(mapMatches.get(maxSameCount));
        }
        double dLv = (double) 100 * maxSameCount / targetCount;
        int realLevel = (int) Math.rint(dLv);
        Boolean isSimilar;
        if (realLevel >= level) {
            isSimilar = true;
        } else {
            isSimilar = false;
        }
        similarInfo.setRealScore(realLevel);
        similarInfo.setIsSimilar(isSimilar);
        return similarInfo;
    }

    /**
     * 字符串转int
     *
     * @param str
     * @return
     */
    public static Integer parseInt(String str) {
        try {
            int i = Integer.parseInt(str);
            return i;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 字符串转int
     *
     * @param str
     * @return
     */
    public static BigDecimal parseDecimal(String str) {
        try {
            BigDecimal bd = new BigDecimal(str);
            return bd;
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 字符串转int
     *
     * @param str
     * @return
     */
    public static Double parseDouble(String str) {
        try {
            Double d = Double.parseDouble(str);
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}
