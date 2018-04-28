package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.CreditBase;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.CreditHint;
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
 * Created by MMM on 2018/03/26.
 * 信用提示,处理分页的表
 */
@Component
public class CreditHintProcessor2 extends PersonCreditProcessor {
    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.CreditHintProcessor2.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        CreditHint creditHint = getCreditInfo(personCreditParam.getTable());
        if (creditHint != null) {
            setCreditEntity(personCredit, creditHint);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取信息实体
     *
     * @return
     */
    public CreditHint getCreditInfo(XWPFTable table) {
        CreditHint creditHint = new CreditHint();
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        XWPFTableRow row;
        XWPFTableCell cell;
        String cellValue;
        rows = table.getRows();
        int rowCount = rows.size();
        int dataIndex = rowCount - 1;//取最后一行为数据行
        for (int i = 0; i < rows.size(); i++) {
            if (i == dataIndex) {
                row = rows.get(i);
                cells = row.getTableCells();
                if (cells.size() == 9) {
                    for (int j = 0; j < cells.size(); j++) {
                        cell = cells.get(j);
                        cellValue = StringUtil.trim(cell.getText());
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        BigDecimal dclCellValue=StringUtil.parseDecimal(strCellValue);
                        if (j == 0) {
                            creditHint.setHouseLoanCount(cellValue);
                            creditHint.setIHouseLoanCount(iCellValue);
                        } else if (j == 1) {
                            creditHint.setOtherLoanCount(cellValue);
                            creditHint.setIOtherLoanCount(iCellValue);
                        } else if (j == 2) {
                            creditHint.setFirstLoanGiveDate(cellValue);
                        } else if (j == 3) {
                            creditHint.setCreditCardCount(cellValue);
                            creditHint.setICreditCardCount(iCellValue);
                        } else if (j == 4) {
                            creditHint.setFirstCreditCardGiveDate(cellValue);
                        } else if (j == 5) {
                            creditHint.setPermitCreditCardCount(cellValue);
                            creditHint.setIPermitCreditCardCount(iCellValue);
                        } else if (j == 6) {
                            creditHint.setFirstPermitCreditCardGiveDate(cellValue);
                        } else if (j == 7) {
                            creditHint.setDeclareCount(cellValue);
                            creditHint.setIDeclareCount(iCellValue);
                        } else if (j == 8) {
                            creditHint.setDissentLableCount(cellValue);
                            creditHint.setIDissentLableCount(iCellValue);
                        }
                    }
                } else if (cells.size() == 10) {
                    for (int j = 0; j < cells.size(); j++) {
                        cell = cells.get(j);
                        cellValue = StringUtil.trim(cell.getText());
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        BigDecimal dclCellValue=StringUtil.parseDecimal(strCellValue);
                        if (j == 0) {
                            creditHint.setHouseLoanCount(cellValue);
                            creditHint.setIHouseLoanCount(StringUtil.parseInt(cellValue));
                        } else if (j == 1) {
                            creditHint.setBusinessHouseLoanCount(cellValue);
                            creditHint.setIBusinessHouseLoanCount(iCellValue);
                        } else if (j == 2) {
                            creditHint.setOtherLoanCount(cellValue);
                            creditHint.setIOtherLoanCount(iCellValue);
                        } else if (j == 3) {
                            creditHint.setFirstLoanGiveDate(cellValue);
                        } else if (j == 4) {
                            creditHint.setCreditCardCount(cellValue);
                            creditHint.setICreditCardCount(iCellValue);
                        } else if (j == 5) {
                            creditHint.setFirstCreditCardGiveDate(cellValue);
                        } else if (j == 6) {
                            creditHint.setPermitCreditCardCount(cellValue);
                            creditHint.setIPermitCreditCardCount(iCellValue);
                        } else if (j == 7) {
                            creditHint.setFirstPermitCreditCardGiveDate(cellValue);
                        } else if (j == 8) {
                            creditHint.setDeclareCount(cellValue);
                            creditHint.setIDeclareCount(iCellValue);
                        } else if (j == 9) {
                            creditHint.setDissentLableCount(cellValue);
                            creditHint.setIDissentLableCount(iCellValue);
                        }
                    }
                }
            }
        }
        return creditHint;
    }

    /**
     * 设置身份信息实体
     *
     * @param personCredit
     * @param creditBase
     */
    public void setCreditEntity(PersonCredit personCredit, CreditBase creditBase) {
        personCredit.setCreditHint((CreditHint) creditBase);
    }
}
