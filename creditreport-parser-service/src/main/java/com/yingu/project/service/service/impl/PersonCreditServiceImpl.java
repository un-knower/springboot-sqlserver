package com.yingu.project.service.service.impl;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.api.ResponseOneResult;
import com.yingu.project.api.Status;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.AnalyseFailRecord;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.persistence.mongo.entity.abridged.AbridgedCredit;
import com.yingu.project.persistence.mongo.repository.AnalyseFailRecordRepository;
import com.yingu.project.persistence.mongo.repository.PersonCreditRepository;
import com.yingu.project.persistence.mysql.entity.XDOrder_Table;
import com.yingu.project.persistence.mysql.mapper.XDOrder_TableMapper;
import com.yingu.project.service.service.PersonCreditProcessor;
import com.yingu.project.service.service.PersonCreditService;
import com.yingu.project.service.service.simple.WordService;
import com.yingu.project.util.config.ApiConfig;
import com.yingu.project.util.config.OssConfig;
import com.yingu.project.util.utils.DateUtil;
import com.yingu.project.util.utils.OssUtil;
import com.yingu.project.util.utils.PoiUtil;
import com.yingu.project.util.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.text.MessageFormat;

/**
 * Created by MMM on 2018/03/21.
 * 个人信用word解析服务
 */
@Service
@Slf4j
public class PersonCreditServiceImpl implements PersonCreditService {
    @Autowired
    PersonCreditRepository personCreditRepository;
    @Autowired
    AnalyseFailRecordRepository analyseFailRecordRepository;
    @Autowired
    ApiConfig apiConfig;
    @Autowired
    OssConfig ossConfig;

    @Autowired
    WordService wordService;
    @Autowired
    XDOrder_TableMapper xdOrder_tableMapper;
    /**
     * 存储个人信用处理器
     */
    private static HashMap<String, PersonCreditProcessor> personCreditProcessorHashMap = new HashMap<>();
    @Autowired
    private List<PersonCreditProcessor> personCreditProcessorList;

    /**
     * 解析文档获取个人信用实体
     *
     * @param xdoc
     * @return
     */
    @Override
    public PersonCredit getPersonCredit(XWPFDocument xdoc, String wordName, String ossFileName) {
        PersonCredit personCredit = new PersonCredit();
        personCredit.setOosFileName(ossFileName);
        personCredit.setWordName(wordName);
        Boolean isSuccess = true;
        String failMessage = "";
        PersonCreditParam personCreditParam;
        int count = 0;//插入实体
        try {
            if (xdoc == null) {
                return null;
            }
            //读取段落,不包含表格
            List<XWPFParagraph> paras = xdoc.getParagraphs();
            for (int i = 0; i < paras.size(); i++) {
                String strParagraph = paras.get(i).getText();
                if (StringUtil.isEmpty(StringUtil.trim(strParagraph))) {
                    continue;
                }
                String processorType = this.getPersonCreditProssorType(strParagraph);
                if (processorType == null) {
                    continue;
                }
                PersonCreditProcessor personCreditProcessor = personCreditProcessorHashMap.get(processorType);
                if (personCreditProcessor == null) {
                    log.error("not get the processor of the type:" + processorType);
                }
                try {
                    personCreditParam = new PersonCreditParam();
                    personCreditParam.setPara(paras.get(i));
                    personCreditParam.setParaIndex(i);
                    personCreditParam.setParas(paras);
                    if (personCreditProcessor.analyse(personCreditParam, personCredit) == true) {
                        count++;
                    }
                } catch (Exception e) {
                    log.error(MessageFormat.format("the processor {0} is analyse error,the wordName is {1}", processorType, wordName));
                    log.error(e.getMessage(), e);
                }
            }

            //遍历表格
            List<XWPFTable> tables = xdoc.getTables();
            List<XWPFTableRow> rows;
            List<XWPFTableCell> cells;
            XWPFTableRow row;
            XWPFTableCell cell;
            int tableIndex=0;
            for (XWPFTable table : tables) {
                tableIndex++;
                //获取处理器类型
                String processorType = getPersonCreditProssorType(table);
                if (processorType == null) {
                    continue;
                }
                //根据类型,得到处理器
                PersonCreditProcessor personCreditProcessor = personCreditProcessorHashMap.get(processorType);
                if (personCreditProcessor == null) {
                    log.error("not get the processor of the type:" + processorType);
                }
                try {
                    personCreditParam = new PersonCreditParam();
                    personCreditParam.setTable(table);
                    personCreditParam.setTables(tables);
                    personCreditParam.setTableIndex(tableIndex);

                    if (personCreditProcessor.analyse(personCreditParam, personCredit) == true) {
                        count++;
                    }
                } catch (Exception e) {
                    log.error(MessageFormat.format("the processor {0} is analyse error,the wordName is {1}", processorType, wordName));
                    log.error(e.getMessage(), e);
                }
            }

            //sj
            personCreditParam = new PersonCreditParam();
            personCreditParam.setXdoc(xdoc);
            personCredit.setWordName(wordName);
            PersonCreditProcessor personCreditProcessor = personCreditProcessorHashMap.get("LoanInfoProcessor");
            if (personCreditProcessor.analyse(personCreditParam, personCredit) == true)
                count++;//三信贷交易信息明细，贷款
            personCreditProcessor = personCreditProcessorHashMap.get("CreditCardProcessor");
            if (personCreditProcessor.analyse(personCreditParam, personCredit) == true)
                count++;
            personCreditProcessor = personCreditProcessorHashMap.get("SemiCreditCardProcessor");
            if (personCreditProcessor.analyse(personCreditParam, personCredit) == true)
                count++;
            personCreditProcessor = personCreditProcessorHashMap.get("InquiryRecordProcessor");
            if (personCreditProcessor.analyse(personCreditParam, personCredit) == true)
                count++;

            personCredit.setCreatetime(DateUtil.getDate());
            personCredit.setWordType("详版");
            if (count == 0) {
                personCredit = null;
                isSuccess = false;
                failMessage = "analyse empty";
            } else {
                String idcard = personCredit.getIdcard();
                //从金管中修复身份证
                String ordernum=wordName.replace(".docx","");
                XDOrder_Table xdOrder_table= xdOrder_tableMapper.selectByOrderNum(ordernum);
                if(xdOrder_table!=null&&xdOrder_table.getUsecard()!=null){
                    personCredit.setJgidcard(xdOrder_table.getUsecard());
                }
                //身份证为空则不保存
                if (!StringUtil.isEmpty(idcard)||!StringUtil.isEmpty(personCredit.getJgidcard())) {

                    if (!StringUtil.isEmpty(personCredit.getReportNo())) {
                        PersonCredit personCreditHis = personCreditRepository.findFirstByReportNo(personCredit.getReportNo());
                        if (personCreditHis != null) {
                            //是否保存重复的报告编号记录
                            if(!apiConfig.getSave_repeat_report()){
                                log.info(MessageFormat.format("the word of {0} has been analysed sucess before", wordName));
                                return personCreditHis;
                            }
                        }
                    }
                    //保存解析结果到mongo
                    personCredit = personCreditRepository.save(personCredit);
                    if (personCredit != null && personCredit.getId() != null) {
                        log.info(MessageFormat.format("the word of {0} has been analysed sucess", wordName));
                    }
                } else {
                    isSuccess = false;
                    failMessage = "analyse idcard is null";
                }
            }
        } catch (Exception e) {
            isSuccess = false;
            failMessage=e.getMessage();
            throw e;
        }
        if (isSuccess == false) {
            AnalyseFailRecord analyseFailRecord = new AnalyseFailRecord();
            analyseFailRecord.setWordName(wordName);
            analyseFailRecord.setCreatetime(DateUtil.getDate());
            analyseFailRecord.setFailMessage(failMessage);
            analyseFailRecord.setOosFileName(ossFileName);
            analyseFailRecordRepository.save(analyseFailRecord);
            log.error(MessageFormat.format("the word of {0} has been analysed failed,message:{1}", wordName,failMessage));
        }
        return personCredit;
    }

    /**
     * 解析文档获取个人信用实体
     * @param filePath
     * @param wordName
     * @return
     * @throws Exception
     */
    @Override
    public ResponseOneResult getPersonCredit(String filePath, String wordName){
        ResponseOneResult result = new ResponseOneResult<>();
        InputStream inputStream=null;
        try {
            File file=new File(filePath);
            if(!file.exists()){
                log.error(MessageFormat.format("wordname is :{0},error msg:{1}",wordName,"the word is not exists"));
                result.setStatus(Status.FAILED);
                result.setMessage("执行异常,请重试");
                return result;
            }
            //上传到阿里云存储
            inputStream = new FileInputStream(filePath);
            String fileName = "";
            if (wordName.lastIndexOf(".") >= 0) {
                fileName = wordName.substring(wordName.lastIndexOf("."));
            }
//            fileName = StringUtil.getUUID() + fileName;
            fileName=wordName;
            if (ossConfig.getIsopen()) {
                Boolean isUploadSucess = OssUtil.upload(inputStream, fileName, ossConfig.getBucketname());
                if (isUploadSucess == false) {
                    result.setStatus(Status.FAILED);
                    result.setMessage("文件上传阿里云失败,请重试");
                    log.error(MessageFormat.format("the word {0} upload oss fail", wordName));
                    return result;
                } else {
                    log.info(MessageFormat.format("the word {0} upload oss sucess", wordName));
                }
            }
            String ossFileName = ossConfig.getViewfile() + fileName;//上传阿里云文件名
            PersonCredit personCredit=null;
            XWPFDocument xdoc = PoiUtil.readWord(filePath);
            String wordType=this.getWordType(xdoc);
            if(wordType.equals("详版")){
                //解析详版
                personCredit = this.getPersonCredit(xdoc, wordName, ossFileName);
                result.setData(personCredit);
            } else if(wordType.equals("简版")){
                AbridgedCredit abridgedCredit= wordService.analysisAbridged(xdoc, wordName, ossFileName);
                log.info("简版解析:"+wordName);
                result.setData(abridgedCredit);
            } else{
                //default:解析详版
                personCredit = this.getPersonCredit(xdoc, wordName, ossFileName);
                result.setData(personCredit);
            }

            result.setStatus(Status.SUCCESS);
            result.setMessage("查询成功");
            return result;
        }
        catch (Exception ex){
            log.error(MessageFormat.format("wordname is :{0},error msg:{1}",wordName, ex.getMessage()) ,ex);
            result.setStatus(Status.FAILED);
            result.setMessage("执行异常,请重试");
            return result;
        }
        finally {
            try {
                inputStream.close();
            }
            catch (Exception ex){
                log.error("inputstream close fail");
            }
        }
    }

    /**
     * 获取信报类型
     * @param xdoc
     * @return 详版;简版
     */
    public String getWordType(XWPFDocument xdoc){
        String wordType="";
        if (xdoc == null) {
            return null;
        }
        //读取段落,不包含表格
        List<XWPFParagraph> paras = xdoc.getParagraphs();
        for (int i = 0; i < paras.size(); i++) {
            String strParagraph = StringUtil.trim(paras.get(i).getText());
            if (StringUtil.isEmpty(strParagraph)) {
                continue;
            }
            if(strParagraph.equals("报告说明")||StringUtil.isSimilar("报告说明",strParagraph,6,0)){
                wordType="详版";
            }
            else if(strParagraph.equals("说明")){
                wordType="简版";
            }
        }
        //段落中没读取到,读取表格
        if(wordType.equals("")){
            List<XWPFTable> tables = xdoc.getTables();
            List<XWPFTableRow> rows;
            List<XWPFTableCell> cells;
            XWPFTableCell cell;

            for (XWPFTable table : tables) {
                String cellValue;
                if (table == null) {
                    return null;
                }
                rows = table.getRows();
                if (rows == null || rows.size() == 0) {
                    return null;
                }
                for (XWPFTableRow row : rows) {
                    for (int i = 0; i < row.getTableCells().size(); i++) {
                        cell = row.getTableCells().get(i);
                        cellValue = StringUtil.trim(cell.getText());
                        if(cellValue.equals("报告说明")||StringUtil.isSimilar("报告说明",cellValue,6,0)){
                            wordType="详版";
                        }
                        else if(cellValue.equals("说明")){
                            wordType="简版";
                        }
                    }
                }
            }
        }
        if(wordType.equals("")){
            wordType="详版";
        }

        return wordType;
    }

    /**
     * 获取个人信用处理器类型
     *
     * @return
     */
    public static String getPersonCreditProssorType(XWPFTable table) {
        List<XWPFTableRow> rows;
        XWPFTableCell cell;
        String cellValue;
        if (table == null) {
            return null;
        }
        rows = table.getRows();
        if (rows == null || rows.size() == 0) {
            return null;
        }
        for (XWPFTableRow row : rows) {
            for (int i = 0; i < row.getTableCells().size(); i++) {
                cell = row.getTableCells().get(i);
                cellValue = StringUtil.trim(cell.getText());
                if (cellValue.indexOf("报告编号") != -1) {
                    return PersonCreditProcessorType.ReportInfoProcessor.getValue();
                } else if (cellValue.equals("被查询者姓名") || StringUtil.isSimilar("被查询者姓名", cellValue, 6)) {
                    return PersonCreditProcessorType.BaseInfoProcessor.getValue();
                } else if (cellValue.equals("性别")) {//个人信息
                    return PersonCreditProcessorType.IdentityProcessor.getValue();
                } else if (cellValue.equals("姓名")) {//配偶信息
                    return PersonCreditProcessorType.SpouseInfoProcessor.getValue();
                } else if (cellValue.equals("居住地址")) {//居住信息
                    return PersonCreditProcessorType.ResidenInfoProcessor.getValue();
                } else if (cellValue.equals("工作单位") || cellValue.equals("单位地址")
                        ||StringUtil.isSimilar("单位地址",cellValue,6,0)) {//职业信息
                    return PersonCreditProcessorType.JobInfoProcessor.getValue();
                } else if (cellValue.equals("住房贷款笔数") || cellValue.equals("其他贷款笔数")
                        ||StringUtil.isSimilar("其他贷款笔数",cellValue,6,0)) {//信用提示
                    return PersonCreditProcessorType.CreditHintProcessor.getValue();
                }else if(cellValue.equals("卡发卡月份")){//信用提示,跨页解析
                    return PersonCreditProcessorType.CreditHintProcessor2.getValue();
                }
                else if (cellValue.equals("贷款逾期")) {//逾期（透支）信息汇总
                    return PersonCreditProcessorType.OverdueInfoCollectProcessor.getValue();
                } else if (cellValue.equals("授信及负债信息概要")) {//一个表格中的授信及负债信息概要
                    return PersonCreditProcessorType.CreditSummaryCollectProcessor.getValue();
                } else if (cellValue.equals("贷款法人机构数")) {//未结清贷款信息汇总
                    return PersonCreditProcessorType.UnClearLoanProcessor.getValue();
                } else if (cellValue.equals("单家行最低授信额") || StringUtil.isSimilar("单家行最低授信额", cellValue)) {
                    if (i + 1 < row.getTableCells().size()) {
                        XWPFTableCell nextCell = row.getTableCells().get(i + 1);
                        if (nextCell.getText().equals("已用额度") || StringUtil.isSimilar("已用额度",nextCell.getText())) {
                            //未销户贷记卡信息汇总
                            return PersonCreditProcessorType.UnCancelCreditCardProcessor.getValue();
                        } else if (nextCell.getText().equals("透支余额")|| StringUtil.isSimilar("透支余额",nextCell.getText())) {
                            //未销户准贷记卡信息汇总
                            return PersonCreditProcessorType.UnCancelPermitCreditCardProcessor.getValue();
                        }
                    }
                } else if (cellValue.equals("担保笔数")) {
                    return PersonCreditProcessorType.ForeignEnsureProcessor.getValue();
                }
            }
        }
        return null;

    }


    private String getPersonCreditProssorType(String str) {
        if (str.indexOf("报告编号") != -1) {
            return PersonCreditProcessorType.ReportInfoProcessor.getValue();
        } else {
            return null;
        }
    }

    /**
     *
     * @param idcard 证件号码
     * @return
     */
    @Override
    public List<PersonCredit> findAllByIdcard(String idcard){
        return personCreditRepository.findAllByIdcardOrWordNameOrderByCreatetimeAsc(idcard,idcard);
    }
    @Override
    public PersonCredit findFirstById(String id){
        return personCreditRepository.findFirstById(id);
    }
    /**
     * 初始化个人信用处理器到map
     *
     * @return
     */
    @PostConstruct
    private void initPersonCreditProssor() {
        if (personCreditProcessorHashMap != null && personCreditProcessorHashMap.size() == 0) {
            for (PersonCreditProcessor personCreditProcessor : personCreditProcessorList) {
                personCreditProcessorHashMap.put(personCreditProcessor.getProcessorType(), personCreditProcessor);
            }
        }
    }
}
