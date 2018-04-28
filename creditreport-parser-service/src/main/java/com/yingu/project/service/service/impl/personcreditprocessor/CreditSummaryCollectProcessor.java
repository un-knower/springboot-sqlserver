package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.UnCancelCreditCardInfo;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.UnCancelPermitCreditCardInfo;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.UnClearLoanInfo;
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
 * 单表中的 未结清贷款信息汇总,未销户贷记卡信息汇总,未销户准贷记卡信息汇
 */
@Component
public class CreditSummaryCollectProcessor extends PersonCreditProcessor {
    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.CreditSummaryCollectProcessor.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        UnClearLoanInfo unClearLoanInfo = new UnClearLoanInfo();
        UnCancelCreditCardInfo unCancelCreditCardInfo = new UnCancelCreditCardInfo();
        UnCancelPermitCreditCardInfo unCancelPermitCreditCardInfo = new UnCancelPermitCreditCardInfo();

        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        XWPFTableRow row;
        XWPFTableCell cell;
        String cellValue;
        rows = personCreditParam.getTable().getRows();
        int rowCount=rows.size();
        int twoIndex=6;
        int threeIndex=9;
        if(rowCount==12){
            twoIndex=7;
            threeIndex=11;
        }
        for (int i = 0; i < rows.size(); i++) {
            row = rows.get(i);
            cells = row.getTableCells();
            if (i == 3)//未结清贷款信息汇总
            {
                for (int j = 0; j < cells.size(); j++) {
                    cellValue = cells.get(j).getText();
                    String strCellValue=StringUtil.trimToNum(cellValue);
                    Integer iCellValue=StringUtil.parseInt(strCellValue);
                    Double dclCellValue=StringUtil.parseDouble(strCellValue);
                    if (j == 0) {
                        unClearLoanInfo.setDaiKuanFaRenJiGouCount(cellValue);
                        unClearLoanInfo.setIDaiKuanFaRenJiGouCount(iCellValue);
                    } else if (j == 1) {
                        unClearLoanInfo.setDaiKuanJiGouCount(cellValue);
                        unClearLoanInfo.setIDaiKuanJiGouCount(iCellValue);
                    } else if (j == 2) {
                        unClearLoanInfo.setLoanCount(cellValue);
                        unClearLoanInfo.setILoanCount(iCellValue);
                    } else if (j == 3) {
                        unClearLoanInfo.setContractAmount(cellValue);
                        unClearLoanInfo.setDclContractAmount(dclCellValue);
                    } else if (j == 4) {
                        unClearLoanInfo.setRemainAmount(cellValue);
                        unClearLoanInfo.setDclRemainAmount(dclCellValue);
                    } else if (j == 5) {
                        unClearLoanInfo.setLast6MonthAvgShoudRefund(cellValue);
                        unClearLoanInfo.setDclLast6MonthAvgShoudRefund(dclCellValue);
                    }

                }
            } else if (i ==twoIndex) {//未销户贷记卡信息汇总
                for (int j = 0; j < cells.size(); j++) {
                    cellValue = cells.get(j).getText();
                    String strCellValue=StringUtil.trimToNum(cellValue);
                    Integer iCellValue=StringUtil.parseInt(strCellValue);
                    Double dclCellValue=StringUtil.parseDouble(strCellValue);
                    if (j == 0) {
                        unCancelCreditCardInfo.setFaKaFaRenJiGouCount(cellValue);
                        unCancelCreditCardInfo.setIFaKaFaRenJiGouCount(iCellValue);
                    } else if (j == 1) {
                        unCancelCreditCardInfo.setFaKaJiGouCount(cellValue);
                        unCancelCreditCardInfo.setIFaKaJiGouCount(iCellValue);
                    } else if (j == 2) {
                        unCancelCreditCardInfo.setAccountCount(cellValue);
                        unCancelCreditCardInfo.setIAccountCount(iCellValue);
                    } else if (j == 3) {
                        unCancelCreditCardInfo.setLoanRightAmount(cellValue);
                        unCancelCreditCardInfo.setDclLoanRightAmount(dclCellValue);
                    } else if (j == 4) {
                        unCancelCreditCardInfo.setHighestLoanRightAmount(cellValue);
                        unCancelCreditCardInfo.setDclHighestLoanRightAmount(dclCellValue);
                    } else if (j == 5) {
                        unCancelCreditCardInfo.setLowestLoanRightAmount(cellValue);
                        unCancelCreditCardInfo.setDclLowestLoanRightAmount(dclCellValue);
                    } else if (j == 6) {
                        unCancelCreditCardInfo.setUsedAmount(cellValue);
                        unCancelCreditCardInfo.setDclUsedAmount(dclCellValue);
                    } else if (j == 7) {
                        unCancelCreditCardInfo.setLast6AvgUseAmount(cellValue);
                        unCancelCreditCardInfo.setDclLast6AvgUseAmount(dclCellValue);
                    }
                }
            } else if (i == threeIndex) {//未销户准贷记卡信息汇总
                for (int j = 0; j < cells.size(); j++) {
                    cellValue = cells.get(j).getText();
                    String strCellValue=StringUtil.trimToNum(cellValue);
                    Integer iCellValue=StringUtil.parseInt(strCellValue);
                    Double dclCellValue=StringUtil.parseDouble(strCellValue);
                    if (j == 0) {
                        unCancelPermitCreditCardInfo.setFaKaFaRenJiGouCount(cellValue);
                        unCancelPermitCreditCardInfo.setIFaKaFaRenJiGouCount(iCellValue);
                    } else if (j == 1) {
                        unCancelPermitCreditCardInfo.setFaKaJiGouCount(cellValue);
                        unCancelPermitCreditCardInfo.setIFaKaJiGouCount(iCellValue);
                    } else if (j == 2) {
                        unCancelPermitCreditCardInfo.setAccountCount(cellValue);
                        unCancelPermitCreditCardInfo.setIAccountCount(iCellValue);
                    } else if (j == 3) {
                        unCancelPermitCreditCardInfo.setLoanRightAmount(cellValue);
                        unCancelPermitCreditCardInfo.setDclLoanRightAmount(dclCellValue);
                    } else if (j == 4) {
                        unCancelPermitCreditCardInfo.setHighestLoanRightAmount(cellValue);
                        unCancelPermitCreditCardInfo.setDclHighestLoanRightAmount(dclCellValue);
                    } else if (j == 5) {
                        unCancelPermitCreditCardInfo.setLowestLoanRightAmount(cellValue);
                        unCancelPermitCreditCardInfo.setDclLowestLoanRightAmount(dclCellValue);
                    } else if (j == 6) {
                        unCancelPermitCreditCardInfo.setOverdraftAmount(cellValue);
                        unCancelPermitCreditCardInfo.setDclOverdraftAmount(dclCellValue);
                    } else if (j == 7) {
                        unCancelPermitCreditCardInfo.setLast6AvgOverdraftAmount(cellValue);
                        unCancelPermitCreditCardInfo.setDclLast6AvgOverdraftAmount(dclCellValue);
                    }
                }
            }

        }
        personCredit.setUnClearLoanInfo(unClearLoanInfo);
        personCredit.setUnCancelCreditCardInfo(unCancelCreditCardInfo);
        personCredit.setUnCancelPermitCreditCardInfo(unCancelPermitCreditCardInfo);
        return true;
    }

}
