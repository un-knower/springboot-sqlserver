package com.yingu.project.persistence.mongo.entity.abridged.credit.creditcard;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * class OverdueInfo
 * 信贷记录
 * 包括：
 * ·信用卡 发生过逾期的贷记卡账户明细
 * ·信用卡 透支超过60天的准贷记卡账户明细
 * ·购房贷款 发生过逾期的账户明细
 * @Date: Created in 2018/4/2 17:44
 * @Author: wm
 */
@Getter
@Setter
public class CreditCard {
    /**发生过逾期的贷记卡账户明细*/
    List<CreditCardOverdueDetails> creditCardOverdueDetailsList;

    /**透支超过90天的准贷记卡账户明细*/
    List<Overdraft60DaysDetails> overdraft60DaysDetailsList;

    /**从未逾期过的贷记卡及透支未超过60天的准贷记卡账户明细*/
    List<BaseCreditCard> unOverdueAndUnOverdraft60DaysDetailsList;
}
