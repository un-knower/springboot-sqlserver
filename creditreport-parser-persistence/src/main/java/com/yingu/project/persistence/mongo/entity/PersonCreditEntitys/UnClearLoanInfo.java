package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by MMM on 2018/03/26.
 * 未结清贷款信息汇总
 */
@Getter
@Setter
public class UnClearLoanInfo extends CreditBase{
    /* 贷款法人机构数 */
    public String daiKuanFaRenJiGouCount;
    public Integer iDaiKuanFaRenJiGouCount;
    /* 贷款机构数 */
    public String daiKuanJiGouCount;
    public Integer iDaiKuanJiGouCount;
    /* 笔数 */
    public String loanCount;
    public Integer iLoanCount;
    /* 合同总额 */
    public String contractAmount;
    public Double dclContractAmount;
    /* 余额 */
    public String remainAmount;
    public Double dclRemainAmount;
    /* 最近六个月平均应还款 */
    public String last6MonthAvgShoudRefund;
    public Double dclLast6MonthAvgShoudRefund;
}
