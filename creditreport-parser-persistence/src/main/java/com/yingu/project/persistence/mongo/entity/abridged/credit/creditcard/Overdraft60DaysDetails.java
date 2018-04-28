package com.yingu.project.persistence.mongo.entity.abridged.credit.creditcard;

import com.yingu.project.persistence.mongo.entity.abridged.credit.common.BaseCredit;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 透支超过90天的准贷记卡账户明细
 * @Date: Created in 2018/4/4 9:26
 * @Author: wm
 */
@Getter
@Setter
public class Overdraft60DaysDetails extends BaseCredit {
    /**透支余额*/
    @NotNull
    BigDecimal overdraftBalance;
    String overdraftBalanceStr;

    /**5年内几个月透支超过60天*/
    @NotNull
    Integer recent5YearsOverdraft60DaysNumOfMonth;

    /**5年内几个月透支超过90天*/
    @NotNull
    Integer recent5YearsOverdraft90DaysNumOfMonth;
}
