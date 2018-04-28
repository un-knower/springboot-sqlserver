package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by MMM on 2018/03/21.
 * 个人信用报告--配偶信息
 */
@Getter
@Setter
public class SpouseInfo extends CreditBase{
    /* 姓名 */
    public String name;
    /* 证件类型 */
    public String cardType;
    /* 证件号码 */
    public String idcard;
    /* 工作单位 */
    public String company;
    /* 联系电话 */
    public String phone;
}
