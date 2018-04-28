package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.CreditBase;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.UnCancelCreditCardInfo;
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
 * 未销户贷记卡信息汇总
 */
@Component
public class UnCancelCreditCardProcessor extends PersonCreditProcessor {
    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.UnCancelCreditCardProcessor.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        UnCancelCreditCardInfo obj= getCreditInfo(personCreditParam.getTable());
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
    public UnCancelCreditCardInfo getCreditInfo(XWPFTable table){
        UnCancelCreditCardInfo unCancelCreditCardInfo=new UnCancelCreditCardInfo();
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
                if(cellKey.equals("发卡法人机构数")||StringUtil.isSimilar("发卡法人机构数",cellKey,"法人")){
                    if(hasValueCell(rows,i,j)){
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        BigDecimal dclCellValue=StringUtil.parseDecimal(strCellValue);
                        unCancelCreditCardInfo.setFaKaFaRenJiGouCount(cellValue);
                        unCancelCreditCardInfo.setIFaKaFaRenJiGouCount(iCellValue);
                    }
                }
                else if(cellKey.equals("发卡机构数")||StringUtil.isSimilar("发卡机构数",cellKey,6,0)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        BigDecimal dclCellValue=StringUtil.parseDecimal(strCellValue);
                        unCancelCreditCardInfo.setFaKaJiGouCount(cellValue);
                        unCancelCreditCardInfo.setIFaKaJiGouCount(iCellValue);
                    }
                }
                else if(cellKey.equals("账户数")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        BigDecimal dclCellValue=StringUtil.parseDecimal(strCellValue);
                        unCancelCreditCardInfo.setAccountCount(cellValue);
                        unCancelCreditCardInfo.setIAccountCount(iCellValue);
                    }
                }
                else if(cellKey.equals("授信总额")||StringUtil.isSimilar("授信总额",cellKey,6,0)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        unCancelCreditCardInfo.setLoanRightAmount(cellValue);
                        unCancelCreditCardInfo.setDclLoanRightAmount(dclCellValue);
                    }
                }
                else if(cellKey.equals("单家行最高授信额")||StringUtil.isSimilar("单家行最高授信额",cellKey,null,"低")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        unCancelCreditCardInfo.setHighestLoanRightAmount(cellValue);
                        unCancelCreditCardInfo.setDclHighestLoanRightAmount(dclCellValue);
                    }
                }
                else if(cellKey.equals("单家行最低授信额")||StringUtil.isSimilar("单家行最低授信额",cellKey,"低")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        unCancelCreditCardInfo.setLowestLoanRightAmount(cellValue);
                        unCancelCreditCardInfo.setDclLowestLoanRightAmount(dclCellValue);
                    }
                }
                else if(cellKey.equals("已用额度")||StringUtil.isSimilar("已用额度",cellKey)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        unCancelCreditCardInfo.setUsedAmount(cellValue);
                        unCancelCreditCardInfo.setDclUsedAmount(dclCellValue);
                    }
                }
                else if(cellKey.equals("最近6个月平均使用额度")||StringUtil.isSimilar("最近6个月平均使用额度",cellKey)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        unCancelCreditCardInfo.setLast6AvgUseAmount(cellValue);
                        unCancelCreditCardInfo.setDclLast6AvgUseAmount(dclCellValue);
                    }
                }
                else{
                    continue;
                }
            }
        }

        return unCancelCreditCardInfo;
    }

    /**
     * 设置身份信息实体
     * @param personCredit
     * @param creditBase
     */
    public void setCreditEntity(PersonCredit personCredit, CreditBase creditBase){
        personCredit.setUnCancelCreditCardInfo((UnCancelCreditCardInfo)creditBase);
    }
}
