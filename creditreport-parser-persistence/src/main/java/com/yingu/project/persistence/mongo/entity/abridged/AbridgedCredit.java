package com.yingu.project.persistence.mongo.entity.abridged;

import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCreditBase;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 简版 个人征信报告
 * @Date: Created in 2018/4/13 14:48
 * @Author: wm
 */
@Getter
@Setter
public class AbridgedCredit extends PersonCreditBase {
    /**头部 基础信息*/
    @NotNull
    private BaseInfo baseInfo;
    /**信用记录*/
    @NotNull
    private CreditDetail creditDetail;
    /**公共记录*/
    private PublicRecord publicRecord;
    /**查询记录明细*/
    private InquiryInfo inquiryInfo;
}
