package com.yingu.project.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by MMM on 2018/04/09.
 * 相似度类
 */
@Getter
@Setter
public class SimilarInfo {
    /* 源字符串 */
    public String strSource;
    /* 比较的字符串 */
    public String strCompared;
    /* 匹配到的字符串 */
    public List<String> matchList;
    /* 真实分数 */
    public Integer realScore;
    /* 是否相似 */
    public Boolean isSimilar;
    /* 匹配到的字符对象 */
    public List<MatchInfo> matchInfo;

}

