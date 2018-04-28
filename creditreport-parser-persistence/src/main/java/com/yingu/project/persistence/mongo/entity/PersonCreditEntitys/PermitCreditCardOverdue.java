package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by MMM on 2018/03/27.
 * 贷款逾期
 */
@Getter
@Setter
public class PermitCreditCardOverdue extends CreditBase{
    /* 笔数 */
    public String accountCount;
    public Integer iAccountCount;
    /* 月份数 */
    public String monthCount;
    public Integer iMonthCount;
    /* 单月最高逾期总额 */
    public String highestOverdraftAmount;
    public Double dclHighestOverdraftAmount;
    /* 最长逾期月数 */
    public String highestOverdraftMonthCount;
    public Integer iHighestOverdraftMonthCount;
}
