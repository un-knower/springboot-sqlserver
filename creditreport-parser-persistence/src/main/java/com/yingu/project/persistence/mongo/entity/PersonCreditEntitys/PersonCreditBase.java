package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by MMM on 2018/04/13.
 */
@Getter
@Setter
public class PersonCreditBase {
    @Id
    private String id;
    /* 报告编号(公共字段) */
    @NotNull
    @Length(min = 15, max = 25)
    private String reportNo;
    /* 查询请求时间(公共字段) */
    private Date requestDateTime;
    private String strRequestDateTime;
    /* 报告时间(公共字段) */
    private Date generateDateTime;
    private String strGenerateDateTime;
    /* 被查询者姓名(公共字段) */
    public String name;
    /* 被查询者证件类型(公共字段) */
    public String cardType;
    /* 被查询者证件号码(公共字段) */
    @NotNull
    @Length(min = 5, max = 20)
    public String idcard;
    /* 阿里云上文件名(公共字段) */
    public String oosFileName;
    /* word文件名称(公共字段) */
    public String wordName;
    /* 信报类型(值:详版;简版)(公共字段) */
    public String wordType;
    /* 创建时间 */
    public Date createtime;
    /* 是否删除 */
    public Boolean isdel = false;
    /* 金管的身份证号 */
    public String jgidcard;
}
