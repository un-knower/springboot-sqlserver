package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by MMM on 2018/03/27.
 * 记录征信报告分析失败记录
 */
@Getter
@Setter
public class AnalyseFailRecord {
    @Id
    private String id;
    /* 报告文件名 */
    public String wordName;
    public Date createtime;
    /* 失败描述 */
    public String failMessage;
    /* 阿里云上文件名(公共字段) */
    public String oosFileName;
}
