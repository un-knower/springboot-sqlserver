package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

/**
 * Created by MMM on 2018/03/21.
 * 个人信用报告
 */
@Getter
@Setter
public class PersonCredit extends PersonCreditBase {

    /* 基本信息 */
    public BaseInfo baseInfo;
    /* 身份信息 */
    public IdentityInfo identityInfo;
    /* 配偶信息 */
    public SpouseInfo spouseInfo;
    /* 居住信息 */
    public List<ResidenInfo> residenInfoList;
    /* 职业信息 */
    public List<JobInfo> jobInfoList;
    /* 信用提示 */
    public CreditHint creditHint;
    /* 未结清贷款信息汇总 */
    public UnClearLoanInfo unClearLoanInfo;
    /* 未销户贷记卡信息汇总 */
    public UnCancelCreditCardInfo unCancelCreditCardInfo;
    /* 未销户准贷记卡信息汇总 */
    public UnCancelPermitCreditCardInfo unCancelPermitCreditCardInfo;
    /* 贷款逾期 */
    public LoanOverdue loanOverdue;
    /* 贷记卡逾期 */
    public CreditCardOverdue creditCardOverdue;
    /* 准贷记卡60天以上透支 */
    public PermitCreditCardOverdue permitCreditCardOverdue;
    /* 对外担保信息汇总 */
    public ForeignEnsure foreignEnsure;
    /* 查询记录 */
    public List<InquiryRecord> inquiryRecordList;
    /* 贷记卡 */
    public List<CreditCard> creditCardList;
    /* 准贷记卡 */
    public List<SemiCreditCard> semiCreditCardList;
    /* 贷款 */
    public List<LoanInfo> loanInfoList;

    //征信报告版本
    private String versionReport;

    //错误记录
    public void setErrorLog(String errorLog) {
        this.errorLog += errorLog;
    }

    private String errorLog = "";

}
