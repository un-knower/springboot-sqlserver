package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.CreditBase;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.UnCancelCreditCardInfo;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.UnCancelPermitCreditCardInfo;
import com.yingu.project.service.service.PersonCreditProcessor;
import com.yingu.project.util.utils.StringUtil;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by MMM on 2018/03/26.
 * 未销户准贷记卡信息汇总
 */
@Component
public class UnCancelPermitCreditCardProcessor extends PersonCreditProcessor {
    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.UnCancelPermitCreditCardProcessor.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        UnCancelPermitCreditCardInfo obj= getCreditInfo(personCreditParam.getTable());
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
    public UnCancelPermitCreditCardInfo getCreditInfo(XWPFTable table){
        UnCancelPermitCreditCardInfo unCancelPermitCreditCardInfo=new UnCancelPermitCreditCardInfo();
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
                        unCancelPermitCreditCardInfo.setFaKaFaRenJiGouCount(cellValue);
                        unCancelPermitCreditCardInfo.setIFaKaFaRenJiGouCount(StringUtil.parseInt(cellValue));
                    }
                }
                else if(cellKey.equals("发卡机构数")||StringUtil.isSimilar("发卡机构数",cellKey,6,0)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        unCancelPermitCreditCardInfo.setFaKaJiGouCount(cellValue);
                        unCancelPermitCreditCardInfo.setIFaKaJiGouCount(StringUtil.parseInt(cellValue));
                    }
                }
                else if(cellKey.equals("账户数")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        unCancelPermitCreditCardInfo.setAccountCount(cellValue);
                        unCancelPermitCreditCardInfo.setIAccountCount(StringUtil.parseInt(cellValue));
                    }
                }
                else if(cellKey.equals("授信总额")||StringUtil.isSimilar("授信总额",cellKey,6,0)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        unCancelPermitCreditCardInfo.setLoanRightAmount(cellValue);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        unCancelPermitCreditCardInfo.setDclLoanRightAmount(dclCellValue);
                    }
                }
                else if(cellKey.equals("单家行最高授信额")||StringUtil.isSimilar("单家行最高授信额",cellKey,null,"低")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        unCancelPermitCreditCardInfo.setHighestLoanRightAmount(cellValue);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        unCancelPermitCreditCardInfo.setDclHighestLoanRightAmount(dclCellValue);
                    }
                }
                else if(cellKey.equals("单家行最低授信额")||StringUtil.isSimilar("单家行最低授信额",cellKey,"低")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        unCancelPermitCreditCardInfo.setLowestLoanRightAmount(cellValue);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        unCancelPermitCreditCardInfo.setDclLowestLoanRightAmount(dclCellValue);
                    }
                }
                else if(cellKey.equals("透支余额")||StringUtil.isSimilar("透支余额",cellKey)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        unCancelPermitCreditCardInfo.setOverdraftAmount(cellValue);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        unCancelPermitCreditCardInfo.setDclOverdraftAmount(dclCellValue);
                    }
                }
                else if(cellKey.equals("最近6个月平均透支余额")||StringUtil.isSimilar("最近6个月平均透支余额",cellKey)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        unCancelPermitCreditCardInfo.setLast6AvgOverdraftAmount(cellValue);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        unCancelPermitCreditCardInfo.setDclLast6AvgOverdraftAmount(dclCellValue);
                    }
                }
                else{
                    continue;
                }
            }
        }

        return unCancelPermitCreditCardInfo;
    }

    /**
     * 设置身份信息实体
     * @param personCredit
     * @param creditBase
     */
    public void setCreditEntity(PersonCredit personCredit, CreditBase creditBase){
        personCredit.setUnCancelPermitCreditCardInfo((UnCancelPermitCreditCardInfo) creditBase);
    }
}
