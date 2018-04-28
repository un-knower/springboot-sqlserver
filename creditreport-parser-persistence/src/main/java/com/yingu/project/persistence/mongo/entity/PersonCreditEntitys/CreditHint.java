package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by MMM on 2018/03/25.
 * 信用提示
 */
@Getter
@Setter
public class CreditHint extends CreditBase{
    /* 住房贷款笔数 */
    public String houseLoanCount;
    public Integer iHouseLoanCount;
    /* 商用(包括商住两用)房贷款笔数 */
    public String businessHouseLoanCount;
    public Integer iBusinessHouseLoanCount;
    /* 其他贷款笔数*/
    public String otherLoanCount;
    public Integer iOtherLoanCount;
    /* 首笔贷款发放月份 */
    public String firstLoanGiveDate;
    /* 贷记卡账户数 */
    public String creditCardCount;
    public Integer iCreditCardCount;
    /* 首张贷记卡发卡月份 */
    public String firstCreditCardGiveDate;
    /* 准贷记卡账户数 */
    public String permitCreditCardCount;
    public Integer iPermitCreditCardCount;
    /* 首张准贷记卡发卡月份 */
    public String firstPermitCreditCardGiveDate;
    /* 本人声明数目 */
    public String declareCount;
    public Integer iDeclareCount;
    /* 异议标注数目 */
    public String dissentLableCount;
    public Integer iDissentLableCount;
}
