package com.yingu.project.persistence.mongo.entity.abridged;

import com.yingu.project.persistence.mongo.entity.abridged.credit.warrant.Warrant;
import com.yingu.project.persistence.mongo.entity.abridged.credit.SummaryInfo;
import com.yingu.project.persistence.mongo.entity.abridged.credit.compensatory.Compensatory;
import com.yingu.project.persistence.mongo.entity.abridged.credit.creditcard.CreditCard;
import com.yingu.project.persistence.mongo.entity.abridged.credit.loan.HouseLoan;
import com.yingu.project.persistence.mongo.entity.abridged.credit.loan.OtherLoan;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 信用记录
 * 这部分包含您的信用卡、贷款和其他信贷记录。金额类数据均以人民币计算，精确到元。
 * @Date: Created in 2018/4/8 15:01
 * @Author: wm
 */
@Getter
@Setter
public class CreditDetail {
    /**信息概要*/
    private SummaryInfo summaryInfo;

    /**保证人代偿信息*/
    private List<Compensatory> compensatoryList;

    /**信用卡*/
    private CreditCard creditCard;

    /**购房贷款*/
    private HouseLoan houseLoan;

    /**其他贷款*/
    private OtherLoan otherLoan;

    /**为他人担保信息*/
    List<Warrant> warrantList;
}
