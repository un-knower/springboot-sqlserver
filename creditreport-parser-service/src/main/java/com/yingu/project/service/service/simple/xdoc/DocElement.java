package com.yingu.project.service.service.simple.xdoc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.annotations.AutomapConstructor;

/**
 * 显示块
 * @Date: Created in 2018/3/29 20:43
 * @Author: wm
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocElement {
    public DocElement(Integer index, String value){
        this.index=index;
        this.value=value;
    }
    /**自定义索引*/
    Integer index;
    /**显示块字符串。一个词匹配一个词，多歌词返回null*/
    String value;
    /**评分。一个词匹配一个词的评分，多歌词匹配多个词的平均评分*/
    Float score;
}
