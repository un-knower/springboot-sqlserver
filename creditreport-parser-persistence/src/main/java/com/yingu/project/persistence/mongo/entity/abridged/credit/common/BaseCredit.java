package com.yingu.project.persistence.mongo.entity.abridged.credit.common;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 信用基类
 * @Date: Created in 2018/4/8 14:36
 * @Author: wm
 */
@Getter
@Setter
public class BaseCredit {

    /**发放日期*/
    @NotNull
    Date distributionDate;
    String distributionDateStr;

    /**发放机构名称*/
    @NotNull
    String distributionAuthority;

    /**
     * 额度
     * 例如：信用额度、贷款金额
     */
    @NotNull
    BigDecimal amount;
    String amountStr;

    /**
     * 信用类型
     * 例如：发卡类型名称、贷款类型
     */
    @NotNull
    String creditType;



    /**最近更新时间*/
    @NotNull
    Date recentUpdateDate;
    String recentUpdateDateStr;
}
