package com.yingu.project.persistence.mongo.entity.abridged.credit.warrant;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 为他人担保信息
 * @Date: Created in 2018/4/9 13:28
 * @Author: wm
 */
@Getter
@Setter
public class Warrant {

    /**日期*/
    String startDateStr;
    /**日期 distributionDate*/
    @NotNull
    Date startDate;

    /**被担保人姓名*/
    @NotNull
    String voucheeName;

    /**证件类型*/
    @NotNull
    String cardType;

    /**证件号码*/
    @NotNull
    String cardNo;

    /**签约机构 distributionAuthority*/
    @NotNull
    String signOrganization;

    /**担保类型 creditType*/
    @NotNull
    String warrantType;

    /**合同金额*/
    String contractAmountStr;
    /**合同金额 amount*/
    @NotNull
    BigDecimal contractAmount;

    /**担保金额*/
    String warrantAmountStr;
    /**担保金额*/
    @NotNull
    BigDecimal warrantAmount;

    /**最新更新日期*/
    String recentUpdateDateStr;
    /**最新更新日期 recentUpdateDateStr*/
    Date recentUpdateDate;

    /**担保贷款余额、担保信用卡已用额度*/
    String warrantRemainAmountStr;
    /**担保贷款余额、担保信用卡已用额度*/
    BigDecimal warrantRemainAmount;
}
