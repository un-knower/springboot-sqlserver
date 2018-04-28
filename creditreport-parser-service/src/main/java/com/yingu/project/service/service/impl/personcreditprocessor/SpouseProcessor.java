package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.CreditBase;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.IdentityInfo;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.SpouseInfo;
import com.yingu.project.service.service.PersonCreditProcessor;
import com.yingu.project.util.utils.DateUtil;
import com.yingu.project.util.utils.StringUtil;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by MMM on 2018/03/21.
 * 身份信息处理器
 */
@Component
public class SpouseProcessor extends PersonCreditProcessor{
    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        SpouseInfo spouseInfo= getCreditInfo(personCreditParam.getTable());
        if(spouseInfo!=null) {
            setCreditEntity(personCredit,spouseInfo);
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
        return PersonCreditProcessorType.SpouseInfoProcessor.getValue();
    }

    /**
     * 获取信息实体
     * @return
     */
    public SpouseInfo getCreditInfo(XWPFTable table){
        SpouseInfo spouseInfo=new SpouseInfo();
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
                    if(cellKey.equals("姓名")){
                        if(hasValueCell(rows,i,j)){
                            cellValue=getCellValue(rows,i,j);
                            spouseInfo.setName(cellValue);
                        }
                    }
                    else if(cellKey.equals("证件类型")||StringUtil.isSimilar("证件类型",cellKey)){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            spouseInfo.setCardType(cellValue);
                        }
                    }
                    else if(cellKey.equals("证件号码")||StringUtil.isSimilar("证件号码",cellKey)){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            spouseInfo.setIdcard(cellValue);
                        }
                    }
                    else if(cellKey.equals("工作单位")||StringUtil.isSimilar("工作单位",cellKey)){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            spouseInfo.setCompany(cellValue);
                        }
                    }
                    else if(cellKey.equals("联系电话")||StringUtil.isSimilar("联系电话",cellKey)){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            spouseInfo.setPhone(cellValue);
                        }
                    }
                    else{
                        continue;
                    }
                }
            }

        return spouseInfo;
    }

    /**
     * 设置身份信息实体
     * @param personCredit
     * @param creditBase
     */
    public void setCreditEntity(PersonCredit personCredit, CreditBase creditBase){
        personCredit.setSpouseInfo((SpouseInfo) creditBase);
    }

}
