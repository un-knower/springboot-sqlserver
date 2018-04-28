package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.*;
import com.yingu.project.service.service.PersonCreditProcessor;
import com.yingu.project.util.utils.StringUtil;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by MMM on 2018/03/26.
 * 未结清贷款信息汇总
 */
@Component
public class UnClearLoanProcessor extends PersonCreditProcessor {
    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.UnClearLoanProcessor.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        UnClearLoanInfo obj= getCreditInfo(personCreditParam.getTable());
        if(obj!=null) {
            setCreditEntity(personCredit,obj);
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * 获取信息实体
     * @return
     */
    public UnClearLoanInfo getCreditInfo(XWPFTable table){
        UnClearLoanInfo unClearLoanInfo=new UnClearLoanInfo();
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        XWPFTableRow row;
        XWPFTableCell cell;
        String cellValue;
        rows = table.getRows();
        for(int i=0;i<rows.size();i++){
            row=rows.get(i);
            cells = row.getTableCells();
            for(int j=0;j<cells.size();j++){
                cell=cells.get(j);
                String cellKey= StringUtil.trim(cell.getText());
                if(cellKey.equals("贷款法人机构数")||StringUtil.isSimilar("贷款法人机构数",cellKey,"法人")){
                    if(hasValueCell(rows,i,j)){
                        cellValue=getCellValue(rows,i,j);
                        unClearLoanInfo.setDaiKuanFaRenJiGouCount(cellValue);
                        unClearLoanInfo.setIDaiKuanFaRenJiGouCount(StringUtil.parseInt(cellValue));
                    }
                }
                else if(cellKey.equals("贷款机构数")||StringUtil.isSimilar("贷款机构数",cellKey,6,0)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        unClearLoanInfo.setDaiKuanJiGouCount(cellValue);
                        unClearLoanInfo.setIDaiKuanJiGouCount(StringUtil.parseInt(cellValue));
                    }
                }
                else if(cellKey.equals("笔数")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        unClearLoanInfo.setLoanCount(cellValue);
                        unClearLoanInfo.setILoanCount(StringUtil.parseInt(cellValue));
                    }
                }
                else if(cellKey.equals("合同总额")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        unClearLoanInfo.setContractAmount(cellValue);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        unClearLoanInfo.setDclContractAmount(dclCellValue);
                    }
                }
                else if(cellKey.equals("余额")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        unClearLoanInfo.setRemainAmount(cellValue);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        unClearLoanInfo.setDclRemainAmount(dclCellValue);
                    }
                }
                else if(cellKey.equals("最近6个月平均应还款")||StringUtil.isSimilar("最近6个月平均应还款",cellKey)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        unClearLoanInfo.setLast6MonthAvgShoudRefund(cellValue);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        unClearLoanInfo.setDclLast6MonthAvgShoudRefund(dclCellValue);
                    }
                }
                else{
                    continue;
                }
            }
        }

        return unClearLoanInfo;
    }

    /**
     * 设置身份信息实体
     * @param personCredit
     * @param creditBase
     */
    public void setCreditEntity(PersonCredit personCredit, CreditBase creditBase){
        personCredit.setUnClearLoanInfo((UnClearLoanInfo)creditBase);
    }
}
