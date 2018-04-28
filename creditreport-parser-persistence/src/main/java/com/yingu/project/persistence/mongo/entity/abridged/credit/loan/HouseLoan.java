package com.yingu.project.persistence.mongo.entity.abridged.credit.loan;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 购房贷款
 * @Date: Created in 2018/4/8 14:59
 * @Author: wm
 */
@Getter
@Setter
public class HouseLoan {
    /**发生过逾期的账户明细*/
    List<Loan> overdueLoanList;
    /**从未逾期过的账户明细*/
    List<Loan> unOverdueLoanList;
}
