package com.yingu.project.persistence.mongo.entity.abridged.credit.compensatory;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 担保人代偿
 * @Date: Created in 2018/4/8 20:22
 * @Author: wm
 */
@Getter
@Setter
public class Compensatory {
    /**代偿日期*/
    String dateStr;
    /**代偿日期*/
    @NotNull
    Date date;

    /**代偿机构*/
    @NotNull
    String organization;

    /**代偿金额*/
    @NotNull
    String amountStr;
    /**代偿金额*/
    @NotNull
    BigDecimal amount;

    /**最近一次还款日期*/
    String latestRepaymentDateStr;
    /**最近一次还款日期*/
    Date latestRepaymentDate;

    /**余额*/
    String amountRemainStr;
    /**余额*/
    @NotNull
    BigDecimal amountRemain;
}
