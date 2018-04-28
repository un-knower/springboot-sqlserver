package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by MMM on 2018/03/22.
 */
@Getter
@Setter
/**
 * 基本信息(报告编号,证件号等)
 */
public class BaseInfo extends CreditBase{

    /* 被查询者姓名 */
    public String name;
    /* 被查询者证件类型 */
    public String cardType;
    /* 被查询者证件号码 */
    public String idcard;
    /* 查询操作员 */
    public String operator;
    /* 查询原因 */
    public String queryReason;

}
