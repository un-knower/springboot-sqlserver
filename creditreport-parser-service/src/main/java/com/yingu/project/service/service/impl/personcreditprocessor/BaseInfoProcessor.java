package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.BaseInfo;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.CreditBase;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.IdentityInfo;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.service.service.PersonCreditProcessor;
import com.yingu.project.util.utils.DateUtil;
import com.yingu.project.util.utils.StringUtil;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by MMM on 2018/03/22.
 * 基础信息
 */
@Component
public class BaseInfoProcessor extends PersonCreditProcessor {
    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {

        BaseInfo baseInfo= getCreditInfo(personCreditParam.getTable());
        if(baseInfo!=null) {
            setCreditEntity(personCredit,baseInfo);
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * 获取处理器类型
     * @return
     */
    @Override
    public String getProcessorType(){
        return PersonCreditProcessorType.BaseInfoProcessor.getValue();
    }
    /**
     * 获取基础信息
     * @return
     */
    public BaseInfo getCreditInfo(XWPFTable table){
        BaseInfo baseInfo=new BaseInfo();
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
                if(cellKey.equals("被查询者姓名")||StringUtil.isSimilar("被查询者姓名",cellKey,"姓名")){
                    if(hasValueCell(rows,i,j)){
                        cellValue=getCellValue(rows,i,j);
                        baseInfo.setName(cellValue);
                    }
                }
                else if(cellKey.equals("被查询者证件类型")||StringUtil.isSimilar("被查询者证件类型",cellKey,"类型")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        baseInfo.setCardType(cellValue);
                    }
                }
                else if(cellKey.equals("被查询者证件号码")||StringUtil.isSimilar("被查询者证件号码",cellKey,"号码")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        baseInfo.setIdcard(cellValue);
                    }
                }
                else if(cellKey.equals("查询操作员")||StringUtil.isSimilar("查询操作员",cellKey,"操作员")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        baseInfo.setOperator(cellValue);
                    }
                }
                else if(cellKey.equals("查询原因")||StringUtil.isSimilar("查询原因",cellKey,"原因")){
                    if(hasValueCell(rows,i,j)) {
                        cellValue=getCellValue(rows,i,j);
                        baseInfo.setQueryReason(cellValue);
                    }
                }
                else{
                    continue;
                }
            }
        }
        //如果身份证没匹配上,则采用指定索引提取
        if(StringUtil.isEmpty( baseInfo.getIdcard())){
            int rowCount = rows.size();
            int dataIndex = rowCount - 1;//取最后一行为数据行
            for (int i = 0; i < rows.size(); i++) {
                if (i == dataIndex) {
                    row = rows.get(i);
                    cells = row.getTableCells();
                        for (int j = 0; j < cells.size(); j++) {
                            cell = cells.get(j);
                            cellValue = StringUtil.trim(cell.getText());
                            if (j == 0) {
                                baseInfo.setName(cellValue);
                            } else if (j == 1) {
                                baseInfo.setCardType(cellValue);
                            } else if (j == 2) {
                                baseInfo.setIdcard(cellValue);
                            } else if (j == 3) {
                                baseInfo.setOperator(cellValue);
                            } else if (j == 4) {
                                baseInfo.setQueryReason(cellValue);
                            }
                        }
                }
            }
        }

        return baseInfo;
    }

    /**
     * 设置信用实体
     * @param personCredit
     * @param creditBase
     */
    public void setCreditEntity(PersonCredit personCredit, CreditBase creditBase){
        BaseInfo baseInfo=(BaseInfo)creditBase;
        personCredit.setBaseInfo(baseInfo);

        personCredit.setName(baseInfo.getName());
        personCredit.setCardType(baseInfo.getCardType());
        personCredit.setIdcard(baseInfo.getIdcard());

    }
}
