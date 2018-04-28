package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.BaseInfo;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.JobInfo;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.ResidenInfo;
import com.yingu.project.service.service.PersonCreditProcessor;
import com.yingu.project.service.service.impl.PersonCreditServiceImpl;
import com.yingu.project.util.utils.DateUtil;
import com.yingu.project.util.utils.StringUtil;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MMM on 2018/03/24.
 */
@Component
public class JobInfoProcessor extends PersonCreditProcessor {
    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.JobInfoProcessor.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        if(personCreditParam.getTableIndex()<personCreditParam.getTables().size()){
            XWPFTable nextTable=personCreditParam.getTables().get(personCreditParam.getTableIndex());
            String tableType= PersonCreditServiceImpl.getPersonCreditProssorType(nextTable);
            if(StringUtil.isEmpty(tableType)){
                for(XWPFTableRow row:nextTable.getRows()){
                    personCreditParam.getTable().addRow(row);
                }
            }
        }

        List<JobInfo> jobInfoList= getCreditInfo(personCreditParam.getTable());
        if(jobInfoList!=null) {
            setCreditEntityList(personCredit,jobInfoList);
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * 获取基础信息
     * @return
     */
    public List<JobInfo> getCreditInfo(XWPFTable table){
        List<JobInfo> jobInfoList=new ArrayList<>();
        JobInfo jobInfo=null;
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        XWPFTableRow row;
        XWPFTableCell cell;
        String cellValue;
        rows = table.getRows();
        List<Integer> noIndexList= new ArrayList<>();

        for(int i=0;i<rows.size();i++){
            row=rows.get(i);
            cells = row.getTableCells();
            for(int j=0;j<cells.size();j++){
                cell=cells.get(j);
                String cellKey= StringUtil.trim(cell.getText());
                if(cellKey.equals("编号")||cellKey.equals("职业")||cellKey.equals("工作单位")){
                    //记录编号索引
                    noIndexList.add(i);
                    break;
                }
            }
        }
        int modelCount=0;
        if(noIndexList.size()>1){
            //记录实体数量
            modelCount=noIndexList.get(1)-noIndexList.get(0)-1;
        }
        for(int i=0;i<modelCount;i++){
            jobInfo=new JobInfo();
            for(int j=0;j<noIndexList.size();j++){
                int index=noIndexList.get(j)+i;
                if(index+1>=rows.size()){
                    break;
                }
                if(j==0){
                    row=rows.get(index+1);
                    cells = row.getTableCells();
                    jobInfo.setJobNo(StringUtil.trim(cells.get(0).getText()));
                    jobInfo.setWorkUnit(StringUtil.trim(cells.get(1).getText()));
                    jobInfo.setAddress(StringUtil.trim(cells.get(2).getText()));
                }
                else if(j==1){
                    row=rows.get(index+1);
                    cells = row.getTableCells();
                    jobInfo.setJob(StringUtil.trim(cells.get(1).getText()));
                    jobInfo.setIndustry(StringUtil.trim(cells.get(2).getText()));
                    jobInfo.setDuty(StringUtil.trim(cells.get(3).getText()));
                    jobInfo.setJobTitle(StringUtil.trim(cells.get(4).getText()));
                    jobInfo.setIncomeDate(cells.get(5).getText());
                    //Date updateTime=DateUtil.StrToDate2(StringUtil.trimToDate(cells.get(6).getText()));
                    Date date=DateUtil.StrToDate2(StringUtil.trimToDate(cells.get(6).getText()));
                    jobInfo.setUpdateTime(date);
                    jobInfo.setStrUpdateTime(cells.get(6).getText());
                }
                else if(j==2){
                    row=rows.get(index+1);
                    cells = row.getTableCells();
                    jobInfo.setDataGenerating(StringUtil.trim(cells.get(1).getText()));
                }
            }
            jobInfoList.add(jobInfo);
        }

        return jobInfoList;
    }
    public void setCreditEntityList(PersonCredit personCredit, List<JobInfo> jobInfoList) {
        personCredit.setJobInfoList(jobInfoList);
    }
}
