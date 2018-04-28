package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

/**
 * 贷款
 */
@Getter
@Setter
public class LoanInfo extends CreditBase {
    /* 结清日期 */
    public String jieQingDate;

    //2012
    /* 贷款机构 */
    public String lendingInstitution;
    /* 业务号 */
    public String businessNo;
    /* 贷款种类细分 */
    public String loanType;
    /* 币种 */
    public String currency;
    /* 发放日期 */
    public String issueDate;
    /* 到期日期 */
    public String dueDate;
    /* 合同金额 */
    public String contractAmount;
    /* 担保方式 */
    public String guarantyStyle;
    /* 还款频率 */
    public String repaymentFrequency;
    /* 还款期数 */
    public String repaymentPeriods;
    /* 状态截止日 */
    public String stateDeadlineDay;

    //明细、2012
    /* 账户状态、状态 */
    public String accountStatus;

    //共同的
    /* 五级分类 */
    public String fiveCategories;
    /* 本金余额 */
    public String balance;
    /* 剩余还款期数 */
    public String remainingRepaymentPeriods;
    /* 本月应还款 */
    public String repayableAmountTheMonth;
    /* 应还款日 */
    public String repayableDate;
    /* 本月实还款 */
    public String paidInAmountTheMonth;
    /* 最近一次还款日期 */
    public String latestRepaymentDate;

    /* 当前逾期期数 */
    public String currentOverduePeriods;
    /* 当前逾期金额 */
    public String currentOverdueAmount;
    /* 逾期31—60天未还本金 */
    public String principleAmount31_60DaysPastDue;
    /* 逾期61—90天未还本金 */
    public String principleAmount61_90DaysPastDue;
    /* 逾期91—180天未还本金 */
    public String principleAmount91_180DaysPastDue;
    /* 逾期180天以上未还本金 */
    public String principleAmount180DaysPlusPastDue;
}
