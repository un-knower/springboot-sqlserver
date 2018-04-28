package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.service.service.PersonCreditProcessor;
import com.yingu.project.util.utils.DateUtil;
import com.yingu.project.util.utils.StringUtil;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by MMM on 2018/03/27.
 */
@Component
public class ReportInfoProcessor extends PersonCreditProcessor {
    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.ReportInfoProcessor.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        if(personCreditParam.getPara()!=null){
            return this.analysePara(personCreditParam,personCredit);
        }
        else if(personCreditParam.getTable()!=null){
            return this.analyseTable(personCreditParam,personCredit);
        }
        else {
            return false;
        }
    }

    /**
     * 解析段落里的信报编号等信息
     * @param personCreditParam
     * @param personCredit
     * @return
     */
    private Boolean analysePara(PersonCreditParam personCreditParam, PersonCredit personCredit){
        String strPara = personCreditParam.getPara().getText();
        Integer paraIndex = personCreditParam.getParaIndex();
        List<XWPFParagraph> paras = personCreditParam.getParas();
        String reportType = "";//报告类别
        String[] strList = strPara.split("\t");
        if (strList.length > 0) {
            String reportNo = strList[0].split(":")[1];
            if (StringUtil.isEmpty(reportNo)) {
                reportType = "2012";//多行
            } else {
                reportType = "2011";//单行
            }
        }
        if (reportType.equals("2011")) {
            if(strList.length>0){
                personCredit.setReportNo(strList[0].split(":")[1]);
            }
            if(strList.length>1){
                String strDate= this.getDate(strList,1);
                Date date= DateUtil.StrToDate3(StringUtil.trimToDate(strDate));
                personCredit.setRequestDateTime(date);
                personCredit.setStrRequestDateTime(strDate);
            }
            if(strList.length>2){
                String strDate= this.getDate(strList,2);
                Date date= DateUtil.StrToDate3(StringUtil.trimToDate(strDate));
                personCredit.setGenerateDateTime(date);
                personCredit.setStrGenerateDateTime(strDate);

            }
        }
        else if(reportType.equals("2012")){
            String strRequestDateTime=null;
            String strGenerateDateTime=null;
            if(!StringUtil.isEmpty(strPara)){
                if(strList.length>1){
                    String strDate= this.getDate(strList,1);
                    strRequestDateTime=strDate;
                }
                if(strList.length>2){
                    String strDate= this.getDate(strList,2);
                    strGenerateDateTime=strDate;
                }
            }
            for(int i=paraIndex+1;i<paraIndex+5;i++){
                XWPFParagraph para= paras.get(i);
                strPara=StringUtil.trim(para.getText());
                if(!StringUtil.isEmpty(strPara)){
                    String [] strList2=strPara.split("\t");
                    if(strList2.length>0){
                        personCredit.setReportNo(strList2[0]);
                    }
                    else if(strList2.length>1){
                        strRequestDateTime+=strList2[1];
                        Date date= DateUtil.StrToDate3(StringUtil.trimToDate(strRequestDateTime));
                        personCredit.setRequestDateTime(date);
                        personCredit.setStrRequestDateTime(strRequestDateTime);
                    }
                    else if(strList2.length>2){
                        strGenerateDateTime+=strList2[2];
                        Date date= DateUtil.StrToDate3(StringUtil.trimToDate(strGenerateDateTime));
                        personCredit.setGenerateDateTime(date);
                        personCredit.setStrGenerateDateTime(strGenerateDateTime);
                    }
                }
            }
        }

        return true;
    }
    /**
     * 解析段落里的信报编号等信息
     * @param personCreditParam
     * @param personCredit
     * @return
     */
    private Boolean analyseTable(PersonCreditParam personCreditParam, PersonCredit personCredit){
        XWPFTable table=personCreditParam.getTable();
        for(XWPFTableRow row :table.getRows()){
            for(XWPFTableCell cell:row.getTableCells()){
                String cellValue=cell.getText();
                if(cellValue.indexOf("报告编号")!=-1){
                    String reportNo = cellValue.split(":")[1];
                    personCredit.setReportNo(reportNo);
                }
                else if(cellValue.indexOf("查询请求时间")!=-1){
                    String strDate= this.getDate(cellValue);
                    Date date= DateUtil.StrToDate3(StringUtil.trimToDate(strDate));
                    personCredit.setRequestDateTime(date);
                    personCredit.setStrRequestDateTime(strDate);                }
                else if(cellValue.indexOf("报告时间")!=-1){
                    String strDate= this.getDate(cellValue);
                    Date date= DateUtil.StrToDate3(StringUtil.trimToDate(strDate));
                    personCredit.setGenerateDateTime(date);
                    personCredit.setStrGenerateDateTime(strDate);
                }
            }
        }
        return true;
    }
    /**
     * 获取日期
     * @param strList
     * @return
     */
    private String getDate(String[] strList,int indexParam){
        //int index=strList[indexParam].indexOf(":")+1;
        int index=0;
        if(strList[indexParam].indexOf("：")!=-1){
            index=strList[indexParam].indexOf("：")+1;
        }
        else {
            index=strList[indexParam].indexOf(":")+1;
        }
        String strDate= strList[indexParam].substring(index);
        return strDate;
    }

    /**
     * 获取日期
     * @param cellValue
     * @return
     */
    private String getDate(String cellValue){
        int index=0;
        if(cellValue.indexOf("：")!=-1){
            index=cellValue.indexOf("：")+1;
        }
        else {
            index=cellValue.indexOf(":")+1;
        }
        String strDate= cellValue.substring(index);
        return strDate;
    }
}
