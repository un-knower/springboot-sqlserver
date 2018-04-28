package com.yingu.project.persistence.mongo.entity.abridged.publicrecord;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 强制执行记录
 * @Date: Created in 2018/4/10 11:33
 * @Author: wm
 */
@Getter
@Setter
public class EnforcementRecord {
    /**执行法院*/
    @NotNull
    String court;

    /**案号*/
    @NotNull
    String orderNo;

    /**执行案由*/
    @NotNull
    String executeReason;

    /**结案方式*/
    @NotNull
    String endType;

    /**立案时间*/
    @NotNull
    Date openDate;
    String openDateStr;

    /**案件状态*/
    @NotNull
    String status;

    /**申请执行标的*/
    @NotNull
    String applyExecute;

    /**已执行标的*/
    @NotNull
    String alreadyExecuted;

    /**申请执行标的金额*/
    @NotNull
    BigDecimal applyAmount;
    String applyAmountStr;

    /**已执行标的金额*/
    @NotNull
    BigDecimal alreadyAmount;
    String alreadyAmountStr;

    /**结案时间*/
    @NotNull
    Date closeDate;
    String closeDateStr;
}
