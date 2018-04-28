package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
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

import java.util.Date;
import java.util.List;

/**
 * Created by MMM on 2018/03/21.
 * 身份信息处理器
 */
@Component
public class IdentityProcessor extends PersonCreditProcessor{

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        IdentityInfo identityInfo= getCreditInfo(personCreditParam.getTable());
        if(identityInfo!=null) {
            setCreditEntity(personCredit,identityInfo);
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
        return PersonCreditProcessorType.IdentityProcessor.getValue();
    }

    /**
     * 获取身份信息
     * @return
     */
    public IdentityInfo getCreditInfo(XWPFTable table){
        IdentityInfo identityInfo=new IdentityInfo();
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
                    if(cellKey.equals("性别")){
                        if(hasValueCell(rows,i,j)){
                            cellValue=getCellValue(rows,i,j);
                            identityInfo.setGender(cellValue);
                        }
                    }
                    else if(cellKey.equals("出生日期")||StringUtil.isSimilar("出生日期",cellKey)){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            Date date=DateUtil.StrToDate2(StringUtil.trimToDate(cellValue));
                            identityInfo.setBirthDay(date);
                            identityInfo.setStrBirthDay(cellValue);
                        }
                    }
                    else if(cellKey.equals("婚姻状况")||StringUtil.isSimilar("婚姻状况",cellKey)){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            identityInfo.setMaritalStatus(cellValue);
                        }
                    }
                    else if(cellKey.equals("手机号码")||StringUtil.isSimilar("手机号码",cellKey)){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            identityInfo.setPhone(cellValue);
                        }
                    }
                    else if(cellKey.equals("单位电话")||StringUtil.isSimilar("单位电话",cellKey,"单位")){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            identityInfo.setCompanyPhone(cellValue);
                        }
                    }
                    else if(cellKey.equals("住宅电话")||StringUtil.isSimilar("住宅电话",cellKey,"住宅")){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            identityInfo.setHousePhone(cellValue);
                        }
                    }
                    else if(cellKey.equals("学历")||StringUtil.isSimilar("学历",cellKey,null,"位")){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            identityInfo.setEducation(cellValue);
                        }
                    }
                    else if(cellKey.equals("学位")){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            identityInfo.setDegree(cellValue);
                        }
                    }
                    else if(cellKey.equals("通讯地址")){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            identityInfo.setContactAddress(cellValue);
                        }
                    }
                    else if(cellKey.equals("户籍地址")){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            identityInfo.setResidenceAddress(cellValue);
                        }
                    }
                    else if(cellKey.equals("数据发生机构名称")||StringUtil.isSimilar("数据发生机构名称",cellKey)){
                        if(hasValueCell(rows,i,j)) {
                            cellValue=getCellValue(rows,i,j);
                            identityInfo.setDataGenerating(cellValue);
                        }
                    }
                    else{
                        continue;
                    }
                }
            }

        return identityInfo;
    }

    /**
     * 设置身份信息实体
     * @param personCredit
     * @param creditBase
     */
    public void setCreditEntity(PersonCredit personCredit, CreditBase creditBase){
        personCredit.setIdentityInfo((IdentityInfo) creditBase);
    }

}
