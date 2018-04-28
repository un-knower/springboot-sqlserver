package com.yingu.project.persistence.mongo.entity.abridged.credit.creditcard;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 发生过逾期的贷记卡账户明细
 * @Date: Created in 2018/4/2 17:54
 * @Author: wm
 */
@Getter
@Setter
public class CreditCardOverdueDetails extends BaseCreditCard {

    /**5年内逾期几个月处于逾期状态*/
    @NotNull
    Integer recent5YearsOverdueNumOfMonth;
    /**5年内逾期几个月逾期超过90天*/
    @NotNull
    Integer recent5YearsOverdue90DaysNumOfMonth;
}
