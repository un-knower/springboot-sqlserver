package com.yingu.project.persistence.mongo.entity.abridged.credit;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 信息概要
 * @Date: Created in 2018/4/2 9:50
 * @Author: wm
 */
@Getter
@Setter
public class SummaryInfo {
    /**资产处置笔数*/
    Integer assetDisposeNum;
    /**保证人代偿笔数*/
    Integer assetCompensatoryNum;



    /**信用卡 账户数（包括：贷记卡、准贷记卡）*/
    @NotNull
    Integer creditCardAccountNum;
    /**购房贷款 账户数（包括：个人住房贷款、商用房贷款）*/
    @NotNull
    Integer houseLoanAccountNum;
    /**其他贷款 账户数*/
    @NotNull
    Integer otherLoanAccountNum;

    /** 信用卡 未结清/未销户账户数 （包括：贷记卡、准贷记卡）*/
    @NotNull
    Integer creditCardUnclearedNum;
    /** 购房贷款  未结清/未销户账户数（包括：个人住房贷款、商用房贷款） */
    @NotNull
    Integer houseLoanUnclearedNum;
    /** 其他贷款  未结清/未销户账户数 */
    @NotNull
    Integer otherLoanUnclearedNum;

    /** 信用卡 发生过逾期的账户数（包括：贷记卡、准贷记卡） */
    @NotNull
    Integer creditCardOverdueNum;
    /** 购房贷款  发生过逾期的账户数（包括：个人住房贷款、商用房贷款） */
    @NotNull
    Integer houseLoanOverdueNum;
    /** 其他贷款  发生过逾期的账户数 */
    @NotNull
    Integer otherLoanOverdueNum;

    /** 信用卡 发生过逾期90天以上的账户数（包括：贷记卡、准贷记卡） */
    @NotNull
    Integer creditCardOverdue90DayNum;
    /** 购房贷款  发生过逾期90天以上的账户数（包括：个人住房贷款、商用房贷款） */
    @NotNull
    Integer houseLoanOverdue90DayNum;
    /** 其他贷款  发生过逾期90天以上的账户数 */
    @NotNull
    Integer otherLoanOverdue90DayNum;


    /** 信用卡 为他人担保笔数（包括：贷记卡、准贷记卡） */
    @NotNull
    Integer creditCardGuaranteeNum;
    /** 购房贷款  为他人担保笔数（包括：个人住房贷款、商用房贷款） */
    @NotNull
    Integer houseLoanGuaranteeNum;
    /** 其他贷款  为他人担保笔数 */
    @NotNull
    Integer otherLoanGuaranteeNum;

}
