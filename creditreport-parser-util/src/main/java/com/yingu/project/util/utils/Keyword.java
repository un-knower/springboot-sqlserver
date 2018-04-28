package com.yingu.project.util.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Keyword {

    private String word;
    /**
     * 类型，1段落para，2表格table
     */
    private int type;
}
