package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.CreditBase;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.ForeignEnsure;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.SpouseInfo;
import com.yingu.project.service.service.PersonCreditProcessor;
import com.yingu.project.util.utils.StringUtil;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by MMM on 2018/03/29.
 */
@Component
public class ForeignEnsureProcessor extends PersonCreditProcessor {
    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.ForeignEnsureProcessor.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        ForeignEnsure spouseInfo= getCreditInfo(personCreditParam.getTable());
        if(spouseInfo!=null) {
            setCreditEntity(personCredit,spouseInfo);
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
    public ForeignEnsure getCreditInfo(XWPFTable table){
        ForeignEnsure foreignEnsure=new ForeignEnsure();
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
                if(cellKey.equals("担保笔数")){
                    if(hasValueCell(rows,i,j)){
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        foreignEnsure.setEnsureCount(cellValue);
                        foreignEnsure.setIEnsureCount(iCellValue);
                    }
                }
                else if(cellKey.equals("担保金额")||StringUtil.isSimilar("担保金额",cellKey,6,0)){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        foreignEnsure.setEnsureAmount(cellValue);
                        foreignEnsure.setDclEnsureAmount(dclCellValue);
                    }
                }
                else if(cellKey.equals("担保本金余额")||StringUtil.isSimilar("担保本金余额",cellKey,"本余")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        String strCellValue=StringUtil.trimToNum(cellValue);
                        Integer iCellValue=StringUtil.parseInt(strCellValue);
                        Double dclCellValue=StringUtil.parseDouble(strCellValue);
                        foreignEnsure.setEnsureRemainAmount(cellValue);
                        foreignEnsure.setDclEnsureRemainAmount(dclCellValue);
                    }
                }
                else{
                    continue;
                }
            }
        }

        return foreignEnsure;
    }
    /**
     * 设置信息实体
     * @param personCredit
     * @param creditBase
     */
    public void setCreditEntity(PersonCredit personCredit, CreditBase creditBase){
        personCredit.setForeignEnsure((ForeignEnsure) creditBase);
    }
}
