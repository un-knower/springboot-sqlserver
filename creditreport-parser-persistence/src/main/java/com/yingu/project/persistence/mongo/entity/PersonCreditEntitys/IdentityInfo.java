package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by MMM on 2018/03/21.
 * 个人信用报告--身份信息
 */
@Getter
@Setter
public class IdentityInfo extends CreditBase{
    /* 性别 */
    public String gender;
    /* 出生日期 */
    public Date birthDay;
    public String strBirthDay;
    /* 婚姻状况 */
    public String maritalStatus;
    /* 手机号码 */
    public String phone;
    /* 手机号码 */
    public String companyPhone;
    /* 住宅电话 */
    public String housePhone;
    /* 学历 */
    public String education;
    /* 学位 */
    public String degree;
    /* 通讯地址 */
    public String contactAddress;
    /* 户籍地址 */
    public String residenceAddress;
    /* 数据发生机构 */
    public String dataGenerating;
}
