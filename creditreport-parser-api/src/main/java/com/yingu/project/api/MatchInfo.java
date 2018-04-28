package com.yingu.project.api;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by MMM on 2018/04/09.
 */
@Setter
@Getter
public class MatchInfo{
    public String matchStr;
//    public Integer targetIndex;
//    public Integer sourceIndex;
    public String sourceStr;
    public Integer targetStartIndex;
    public Integer targetEndIndex;
    public Integer sourceStartIndex;
    public Integer sourceEndIndex;
}
