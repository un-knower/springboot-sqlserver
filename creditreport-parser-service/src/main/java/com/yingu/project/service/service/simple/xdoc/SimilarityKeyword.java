package com.yingu.project.service.service.simple.xdoc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 模糊查询关键词
 * @Date: Created in 2018/4/9 11:06
 * @Author: wm
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimilarityKeyword {
    /**关键词*/
    String keyword;
    /**匹配分值0-100*/
    Integer score;
}
