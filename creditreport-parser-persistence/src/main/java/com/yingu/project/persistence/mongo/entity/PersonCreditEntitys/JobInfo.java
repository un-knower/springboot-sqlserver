package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by MMM on 2018/03/23.
 * 职业信息
 */
@Getter
@Setter
public class JobInfo  extends CreditBase{
    /* 编号 */
    public String jobNo;
    /* 工作单位 */
    public String workUnit;
    /* 工作地址 */
    public String address;
    /* 职业 */
    public String job;
    /* 行业 */
    public String industry;
    /* 职务 */
    public String duty;
    /* 职称 */
    public String jobTitle;
    /* 进入本单位年份 */
    public String incomeDate;
    /* 信息更新日期 */
    public Date updateTime;
    public String strUpdateTime;
    /* 数据发生机构 */
    public String dataGenerating;
}
