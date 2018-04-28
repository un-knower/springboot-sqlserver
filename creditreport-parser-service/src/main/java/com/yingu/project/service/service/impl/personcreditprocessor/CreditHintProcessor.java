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

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by MMM on 2018/03/26.
 * 信用提示
 */
@Component
public class CreditHintProcessor extends PersonCreditProcessor {
    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.CreditHintProcessor.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        CreditHint creditHint= getCreditInfo(personCreditParam.getTable());
        if(creditHint!=null) {
            setCreditEntity(personCredit,creditHint);
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
    public CreditHint getCreditInfo(XWPFTable table){
        CreditHint creditHint=new CreditHint();
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
                if(cellKey.equals("个人住房贷款笔数")||cellKey.equals("住房贷款笔数")
                        ||StringUtil.isSimilar("住房贷款笔数",cellKey,"住房")){
                    if(hasValueCell(rows,i,j)){
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        BigDecimal dclCellValue=StringUtil.parseDecimal(strCellValue);
                        creditHint.setHouseLoanCount(cellValue);
                        creditHint.setIHouseLoanCount(iCellValue);
                    }
                }
                else if(cellKey.equals("个人商用房（包括商住两用）贷款笔数")||StringUtil.isSimilar("个人商用房（包括商住两用）贷款笔数",cellKey)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        BigDecimal dclCellValue=StringUtil.parseDecimal(strCellValue);
                        creditHint.setBusinessHouseLoanCount(cellValue);
                        creditHint.setIBusinessHouseLoanCount(iCellValue);
                    }
                }
                else if(cellKey.equals("其他贷款笔数")||StringUtil.isSimilar("其他贷款笔数",cellKey,"其他")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        BigDecimal dclCellValue=StringUtil.parseDecimal(strCellValue);
                        creditHint.setOtherLoanCount(cellValue);
                        creditHint.setIOtherLoanCount(iCellValue);
                    }
                }
                else if(cellKey.equals("首笔贷款发放月份")||StringUtil.isSimilar("首笔贷款发放月份",cellKey,"放","卡")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        creditHint.setFirstLoanGiveDate(cellValue);
                    }
                }
                else if(cellKey.equals("贷记卡账户数")||StringUtil.isSimilar("贷记卡账户数",cellKey,null,"准")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        BigDecimal dclCellValue=StringUtil.parseDecimal(strCellValue);
                        creditHint.setCreditCardCount(cellValue);
                        creditHint.setICreditCardCount(iCellValue);
                    }
                }
                else if(cellKey.equals("首张贷记卡发卡月份")||StringUtil.isSimilar("首张贷记卡发卡月份",cellKey,null,"准")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        creditHint.setFirstCreditCardGiveDate(cellValue);
                    }
                }
                else if(cellKey.equals("准贷记卡账户数")||StringUtil.isSimilar("准贷记卡账户数",cellKey,"准")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        BigDecimal dclCellValue=StringUtil.parseDecimal(strCellValue);
                        creditHint.setPermitCreditCardCount(cellValue);
                        creditHint.setIPermitCreditCardCount(iCellValue);
                    }
                }
                else if(cellKey.equals("首张准贷记卡发卡月份")||StringUtil.isSimilar("首张准贷记卡发卡月份",cellKey,"准")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        creditHint.setFirstPermitCreditCardGiveDate(cellValue);
                    }
                }
                else if(cellKey.equals("本人声明数目")||StringUtil.isSimilar("本人声明数目",cellKey)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        BigDecimal dclCellValue=StringUtil.parseDecimal(strCellValue);
                        creditHint.setDeclareCount(cellValue);
                        creditHint.setIDeclareCount(iCellValue);
                    }
                }
                else if(cellKey.equals("异议标注数目")||StringUtil.isSimilar("异议标注数目",cellKey)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        BigDecimal dclCellValue=StringUtil.parseDecimal(strCellValue);
                        creditHint.setDissentLableCount(cellValue);
                        creditHint.setIDissentLableCount(iCellValue);
                    }
                }
                else{
                    continue;
                }
            }
        }

        return creditHint;
    }

    /**
     * 设置身份信息实体
     * @param personCredit
     * @param creditBase
     */
    public void setCreditEntity(PersonCredit personCredit, CreditBase creditBase){
        personCredit.setCreditHint((CreditHint) creditBase);
    }
}
