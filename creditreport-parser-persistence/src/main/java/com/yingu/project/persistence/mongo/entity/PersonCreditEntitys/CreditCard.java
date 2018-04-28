package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

/**
 * 贷记卡
 */
@Getter
@Setter
public class CreditCard extends CreditBase {
    //2012
    /* 发卡机构 */
    public String cardIssuer;
    /* 业务号 */
    public String businessNo;
    /* 币种 */
    public String currency;
    /* 发卡日期 */
    public String issueDate;
    /* 授信额度 */
    public String lineOfCredit;
    /* 担保方式 */
    public String guarantyStyle;
    /* 状态截止日 */
    public String stateDeadlineDay;

    //2012、2011
    /* 共享额度 */
    public String sharedCredit;

    //明细、2012
    /* 账户状态、状态 */
    public String accountStatus;

    //共同的
    /* 已用额度 */
    public String consumedCredit;
    /* 最近6个月平均使用额度 */
    public String averageConsumedCreditInRecent6Months;
    /* 最大使用额度 */
    public String maximumUseCredit;
    /* 本月应还款 */
    public String repayableAmountOfTheMonth;

    /* 账单日 */
    public String statementDate;
    /* 本月实还款 */
    public String paidInAmountOfTheMonth;
    /* 最近一次还款日期 */
    public String latestRepaymentDate;
    /* 当前逾期期数 */
    public String currentOverdueInstallment;
    /* 当前逾期金额 */
    public String currentOverdueAmount;
}
