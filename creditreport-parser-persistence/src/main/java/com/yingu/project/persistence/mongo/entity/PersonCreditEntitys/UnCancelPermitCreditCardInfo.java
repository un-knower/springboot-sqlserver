package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by MMM on 2018/03/26.
 * 未销户准贷记卡信息汇总
 */
@Getter
@Setter
public class UnCancelPermitCreditCardInfo extends CreditBase{
    /* 发卡法人机构数 */
    public String faKaFaRenJiGouCount;
    public Integer iFaKaFaRenJiGouCount;
    /* 发卡机构数 */
    public String faKaJiGouCount;
    public Integer iFaKaJiGouCount;
    /* 账户数 */
    public String accountCount;
    public Integer iAccountCount;
    /* 授信总额 */
    public String loanRightAmount;
    public Double dclLoanRightAmount;
    /* 单家行最高授权信额 */
    public String highestLoanRightAmount;
    public Double dclHighestLoanRightAmount;
    /* 单家行最低授权信额 */
    public String lowestLoanRightAmount;
    public Double dclLowestLoanRightAmount;
    /* 透支余额 */
    public String overdraftAmount;
    public Double dclOverdraftAmount;
    /* 最近6个月平均透支余额 */
    public String last6AvgOverdraftAmount;
    public Double dclLast6AvgOverdraftAmount;
}
