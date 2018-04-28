package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by MMM on 2018/03/29.
 * 对外担保信息汇总
 */
@Getter
@Setter
public class ForeignEnsure extends CreditBase{
    /* 担保笔数 */
    public String ensureCount;
    public Integer iEnsureCount;
    /* 担保金额 */
    public String ensureAmount;
    public Double dclEnsureAmount;
    /* 担保本金余额 */
    public String ensureRemainAmount;
    public Double dclEnsureRemainAmount;
}
