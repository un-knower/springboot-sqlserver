package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.CreditCardOverdue;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.LoanOverdue;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PermitCreditCardOverdue;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.service.service.PersonCreditProcessor;
import com.yingu.project.util.utils.StringUtil;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by MMM on 2018/03/27.
 * 逾期（透支〉信息汇总
 */
@Component
public class OverdueInfoCollectProcessor extends PersonCreditProcessor {
    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.OverdueInfoCollectProcessor.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        LoanOverdue loanOverdue = new LoanOverdue();
        CreditCardOverdue creditCardOverdue = new CreditCardOverdue();
        PermitCreditCardOverdue permitCreditCardOverdue = new PermitCreditCardOverdue();

        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        XWPFTableRow row;
        XWPFTableCell cell;
        String cellValue;
        rows = personCreditParam.getTable().getRows();
        int rowCount = rows.size();
        int dataIndex = rowCount - 1;//取最后一行为数据行
        for (int i = 0; i < rows.size(); i++) {
            row = rows.get(i);
            cells = row.getTableCells();
            if (i == dataIndex)//未结清贷款信息汇总
            {
                for (int j = 0; j < cells.size(); j++) {
                    cellValue = cells.get(j).getText();
                    String strCellValue=StringUtil.trimToNum(cellValue);
                    Integer iCellValue=StringUtil.parseInt(strCellValue);
                    Double dclCellValue=StringUtil.parseDouble(strCellValue);
                    if (j == 0) {
                        loanOverdue.setAccountCount(cellValue);
                        loanOverdue.setIAccountCount(iCellValue);
                    } else if (j == 1) {
                        loanOverdue.setMonthCount(cellValue);
                        loanOverdue.setIMonthCount(iCellValue);
                    } else if (j == 2) {
                        loanOverdue.setHighestOverdueAmount(cellValue);
                        loanOverdue.setDclHighestOverdueAmount(dclCellValue);
                    } else if (j == 3) {
                        loanOverdue.setHighestOverdueMonthCount(cellValue);
                        loanOverdue.setIHighestOverdueMonthCount(iCellValue);
                    }

                    else if (j == 4) {
                        creditCardOverdue.setAccountCount(cellValue);
                        creditCardOverdue.setIAccountCount(iCellValue);
                    } else if (j == 5) {
                        creditCardOverdue.setMonthCount(cellValue);
                        creditCardOverdue.setIMonthCount(iCellValue);
                    }else if (j == 6) {
                        creditCardOverdue.setHighestOverdueAmount(cellValue);
                        creditCardOverdue.setDclHighestOverdueAmount(dclCellValue);
                    } else if (j == 7) {
                        creditCardOverdue.setHighestOverdueMonthCount(cellValue);
                        creditCardOverdue.setIHighestOverdueMonthCount(iCellValue);
                    }

                    else if (j == 8) {
                        permitCreditCardOverdue.setAccountCount(cellValue);
                        permitCreditCardOverdue.setIAccountCount(iCellValue);
                    } else if (j == 9) {
                        permitCreditCardOverdue.setMonthCount(cellValue);
                        permitCreditCardOverdue.setIMonthCount(iCellValue);
                    }else if (j == 10) {
                        permitCreditCardOverdue.setHighestOverdraftAmount(cellValue);
                        permitCreditCardOverdue.setDclHighestOverdraftAmount(dclCellValue);
                    } else if (j == 11) {
                        permitCreditCardOverdue.setHighestOverdraftMonthCount(cellValue);
                        permitCreditCardOverdue.setIHighestOverdraftMonthCount(iCellValue);
                    }
                }
            }
        }
        personCredit.setLoanOverdue(loanOverdue);
        personCredit.setCreditCardOverdue(creditCardOverdue);
        personCredit.setPermitCreditCardOverdue(permitCreditCardOverdue);
        return true;
    }
}
