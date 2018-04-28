package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

/**
 * 查询记录
 */
@Getter
@Setter
public class InquiryRecord extends CreditBase {
    /* 查询曰期 */
    public String queryDate;
    /* 査询操作员 */
    public String queryOperator;
    /* 査询原因 */
    public String queryCause;
}
