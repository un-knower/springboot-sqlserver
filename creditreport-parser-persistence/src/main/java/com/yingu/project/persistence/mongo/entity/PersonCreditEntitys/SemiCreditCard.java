package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

/**
 * 准贷记卡
 */
@Getter
@Setter
public class SemiCreditCard extends CreditBase {
    /* 发卡日期 */
    public String issueDate;
    /* 发卡机构 */
    public String cardIssuer;
    /* 币种 */
    public String currency;
    /* 业务号 */
    public String businessNo;
    /* 授信额度 */
    public String lineOfCredit;
    /* 担保方式 */
    public String guarantyStyle;
    /* 状态截止日 */
    public String stateDeadlineDay;

    //明细
    /* 账户状态 */
    public String accountStatus;

    //2011
    /* 共享额度 */
    public String sharedCredit;

    /* 透支余额 */
    public String overdraftBalance;
    /* 最近6个月平均透支余额 */
    public String averageOverdraftBalanceInRecent6Months;
    /* 最大透支余额 */
    public String maximumOverdraftBalance;

    /* 账单日 */
    public String statementDate;
    /* 本月实还款 */
    public String paidInAmountOfTheMonth;
    /* 最近一次还款日期 */
    public String latestRepaymentDate;
    /* 透支180天以上未付余额 */
    public String overdraftBalance180DaysPlusUnpaid;
}
