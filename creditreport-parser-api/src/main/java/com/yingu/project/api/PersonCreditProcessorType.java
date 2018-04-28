package com.yingu.project.api;

/**
 * Created by MMM on 2018/03/21.
 */
public enum PersonCreditProcessorType {
    IdentityProcessor("IdentityProcessor", "IdentityProcessor"),
    LoanInfoProcessor("LoanInfoProcessor", "LoanInfoProcessor"),
    CreditCardProcessor("CreditCardProcessor", "CreditCardProcessor"),
    SemiCreditCardProcessor("SemiCreditCardProcessor", "SemiCreditCardProcessor"),
    InquiryRecordProcessor("InquiryRecordProcessor", "InquiryRecordProcessor"),
    BaseInfoProcessor("BaseInfoProcessor", "BaseInfoProcessor"),
    SpouseInfoProcessor("SpouseInfoProcessor", "SpouseInfoProcessor"),
    ResidenInfoProcessor("ResidenInfoProcessor", "ResidenInfoProcessor"),
    JobInfoProcessor("JobInfoProcessor", "JobInfoProcessor"),
    CreditHintProcessor("CreditHintProcessor", "CreditHintProcessor"),
    CreditHintProcessor2("CreditHintProcessor2", "CreditHintProcessor2"),
    UnClearLoanProcessor("UnClearLoanProcessor", "UnClearLoanProcessor"),
    UnCancelCreditCardProcessor("UnCancelCreditCardProcessor", "UnCancelCreditCardProcessor"),
    UnCancelPermitCreditCardProcessor("UnCancelPermitCreditCardProcessor", "UnCancelPermitCreditCardProcessor"),
    CreditSummaryCollectProcessor("CreditSummaryCollectProcessor", "CreditSummaryCollectProcessor"),
    OverdueInfoCollectProcessor("OverdueInfoCollectProcessor", "OverdueInfoCollectProcessor"),
    ReportInfoProcessor("ReportInfoProcessor","ReportInfoProcessor"),
    ForeignEnsureProcessor("ForeignEnsureProcessor","ForeignEnsureProcessor");
    String name;
    String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    PersonCreditProcessorType(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
