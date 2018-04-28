package com.yingu.project.persistence.mongo.entity.abridged.credit.loan;

import com.yingu.project.persistence.mongo.entity.abridged.credit.common.BaseCredit;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 贷款正常
 * @Date: Created in 2018/4/8 14:22
 * @Author: wm
 */
@Getter
@Setter
public class Loan extends BaseCredit{

    /**到期日期*/
    Date expireDate;
    String expireDateStr;

    /**贷款余额*/
    BigDecimal loanBalance;
    String loanBalanceStr;



    /**结清日期*/
    Date closeDate;
    String closeDateStr;



    /**逾期金额*/
    BigDecimal overdueAmount;
    String overdueAmountStr;

    /**5年内逾期几个月处于逾期状态*/
    Integer recent5YearsOverdueNumOfMonth;
    /**5年内逾期几个月逾期超过90天*/
    Integer recent5YearsOverdue90DaysNumOfMonth;
}
