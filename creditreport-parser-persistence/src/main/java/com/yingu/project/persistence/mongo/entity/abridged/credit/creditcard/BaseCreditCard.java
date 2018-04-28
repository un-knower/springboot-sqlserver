package com.yingu.project.persistence.mongo.entity.abridged.credit.creditcard;

import com.yingu.project.persistence.mongo.entity.abridged.credit.common.BaseCredit;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 信用卡基类
 * @Date: Created in 2018/4/4 9:16
 * @Author: wm
 */
@Getter
@Setter
public class BaseCreditCard extends BaseCredit{

    /*信用卡已使用额度*/
    BigDecimal creditCardUsedAmount;
    String creditCardUsedAmountStr;
}
