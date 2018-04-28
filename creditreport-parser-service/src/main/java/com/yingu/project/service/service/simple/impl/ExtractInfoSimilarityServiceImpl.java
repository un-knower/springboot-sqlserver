package com.yingu.project.service.service.simple.impl;

import com.yingu.project.api.MatchInfo;
import com.yingu.project.persistence.mongo.entity.abridged.CreditDetail;
import com.yingu.project.persistence.mongo.entity.abridged.InquiryInfo;
import com.yingu.project.persistence.mongo.entity.abridged.PublicRecord;
import com.yingu.project.persistence.mongo.entity.abridged.credit.warrant.Warrant;
import com.yingu.project.service.service.simple.ExtractInfoSimilarityService;
import com.yingu.project.persistence.mongo.entity.abridged.BaseInfo;
import com.yingu.project.persistence.mongo.entity.abridged.credit.SummaryInfo;
import com.yingu.project.persistence.mongo.entity.abridged.credit.common.BaseCredit;
import com.yingu.project.persistence.mongo.entity.abridged.credit.compensatory.Compensatory;
import com.yingu.project.persistence.mongo.entity.abridged.credit.creditcard.CreditCard;
import com.yingu.project.persistence.mongo.entity.abridged.credit.loan.HouseLoan;
import com.yingu.project.persistence.mongo.entity.abridged.credit.loan.Loan;
import com.yingu.project.persistence.mongo.entity.abridged.credit.loan.OtherLoan;
import com.yingu.project.persistence.mongo.entity.abridged.inquiryinfo.InquiryDetails;
import com.yingu.project.persistence.mongo.entity.abridged.credit.creditcard.*;
import com.yingu.project.persistence.mongo.entity.abridged.publicrecord.EnforcementRecord;
import com.yingu.project.service.service.simple.cache.ThreadCache;
import com.yingu.project.service.service.simple.xdoc.*;
import com.yingu.project.util.utils.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 相似匹配
 *
 * @Date: Created in 2018/3/26 10:40
 * @Author: wm
 */
@Service
@Slf4j
@Scope(value = "prototype")
public class ExtractInfoSimilarityServiceImpl implements ExtractInfoSimilarityService {

    @Autowired
    XdocService xdocService;

    @Autowired
    ThreadCache threadCache;

    /**
     * 基础信息
     *
     * @param xdoc
     */
    @Override
    @SneakyThrows
    public BaseInfo getBaseInfo(XWPFDocument xdoc) {
        BaseInfo baseInfo = new BaseInfo();

//        List<DocElement> list = xdocService.getDocElementListBetweenKeywords(xdoc, null, new SimilarityKeyword("信贷记录", 66));
        //段落提取
        XWPFParagraph paragraph = xdocService.getParagraph(xdoc, new String[]{"报告编号", "查询时间", "报告时间", "证件类型", "证件号码"});
        if (null == paragraph) {
            return null;
        }
        String text = paragraph.getText();
        if (StringUtil.isEmpty(text)){
            return null;
        }
        if (text.length()<100){
            XWPFParagraph paragraphNext = xdocService.getParagraph(xdoc, new String[]{"姓名", "证件类型", "证件号码"});
            if (null == paragraphNext) {
                return null;
            }
            String textNext = paragraphNext.getText();
            if (!StringUtil.isEmpty(textNext)) {
                text = text.concat("  ").concat(textNext);
            }
        }
        if (!StringUtil.isEmpty(text)) {
            text = StringUtils.replaceAll(text, "\n", " ").replaceAll("\t", " ");
        }

        //报告编号
        if (true) {
            String temp = XdocUtil.substringFuzzyBetween(text, new SimilarityKeyword("报告编号:", 66), new SimilarityKeyword(" ", 100));
            if (!StringUtil.isEmpty(temp)){
                temp = temp.trim();
                baseInfo.setContractNo(temp);
            }
        }
        //查询时间
        if (true) {
            MatchInfo matchInfo = XdocUtil.fuzzySearchFirstMatch(text, new SimilarityKeyword("报告时间", 66));
            String temp = null;
            if (matchInfo!=null){
                temp = XdocUtil.substringFuzzyBetween(text, new SimilarityKeyword("査询时间:", 66), new SimilarityKeyword(matchInfo.getMatchStr(), 100));
            }else{
                temp = XdocUtil.substringFuzzyBetween(text, new SimilarityKeyword("査询时间:", 66), new SimilarityKeyword(" ", 100));
            }
            if (!StringUtil.isEmpty(temp)){
                temp = temp.trim();
                baseInfo.setInquiryDateTimeStr(temp);

                temp = temp.replaceAll(":", "");
                temp = StringUtils.deleteWhitespace(temp);
                Date date = XdocUtil.getDateValue(temp, "yyyy.MM.ddHHmmss");
                if (date!=null){
                    baseInfo.setInquiryDateTime(date);
                }
            }
        }
        //报告时间
        if (true) {
            String temp = XdocUtil.substringFuzzyAfter(text, new SimilarityKeyword("查询时间", 66));
            if (!StringUtil.isEmpty(temp)){
                temp = XdocUtil.substringFuzzyBetween(temp, new SimilarityKeyword("报告时间:", 60), new SimilarityKeyword(" ", 100));
            }else{
                temp = XdocUtil.substringFuzzyBetween(text, new SimilarityKeyword("报告时间:", 66), new SimilarityKeyword(" ", 100));
            }
            if (!StringUtil.isEmpty(temp)){
                temp = temp.trim();
                baseInfo.setGenerationDateTimeStr(temp);

                temp = temp.replaceAll(":", "");
                temp = StringUtils.deleteWhitespace(temp);
                Date date = XdocUtil.getDateValue(temp, "yyyy.MM.ddHHmmss");
                if (date!=null){
                    baseInfo.setGenerationDateTime(date);
                }
            }
        }
        //姓名
        if (true) {
            String temp = XdocUtil.substringFuzzyBetween(text, new SimilarityKeyword("姓名:", 66), new SimilarityKeyword(" ", 100));
            if (!StringUtil.isEmpty(temp)){
                temp = temp.trim();
                baseInfo.setClientName(temp);
            }
        }
        //证件类型
        if (true) {
            String temp = XdocUtil.substringFuzzyBetween(text, new SimilarityKeyword("证件类型:", 66), new SimilarityKeyword(" ", 100));
            if (!StringUtil.isEmpty(temp)){
                temp = temp.trim();
                baseInfo.setCardType(temp);
            }
        }
        //是否已婚
        if (true) {
            MatchInfo matchInfo = XdocUtil.fuzzySearchFirstMatch(text, new SimilarityKeyword("已婚", 66));
            if (matchInfo==null){
                matchInfo = XdocUtil.fuzzySearchFirstMatch(text, new SimilarityKeyword("未婚", 66));
            }
            if (matchInfo!=null){
                String temp = matchInfo.getMatchStr().trim();
                baseInfo.setIsMarried(temp);
            }
        }
        //证件号码
        if (true) {
            String temp = XdocUtil.substringFuzzyAfter(text, new SimilarityKeyword("证件号码", 66));
            if (!StringUtil.isEmpty(temp)){
                if (!StringUtil.isEmpty(baseInfo.getIsMarried())) {
                    String temp2 = XdocUtil.substringFuzzyBetween(temp, new SimilarityKeyword(":", 100), new SimilarityKeyword(baseInfo.getIsMarried(), 100));
                    if (StringUtil.isEmpty(temp2)){
                        temp2 = XdocUtil.substringBefore(temp, baseInfo.getIsMarried());
                    }
                    temp = temp2;
                }else{
                    temp = XdocUtil.substringFuzzyBetween(temp, new SimilarityKeyword(":", 100), new SimilarityKeyword(" ", 100));
                }
                if (!StringUtil.isEmpty(temp)){
                    temp = temp.trim();
                    baseInfo.setCardNo(temp);
                }
            }
        }
        return baseInfo;

//        //片段提取
//        XWPFRun contractNumRun = xdocService.getRun(paragraph, new String[]{"报告编号", "姓名"});
//        if (null != contractNumRun) {
//            String runStr = contractNumRun.getPictureText();
//            //报告编号
//            BaseInfoContractNo baseInfoContractNo = new BaseInfoContractNo();
//            baseInfoContractNo.analysisValue(runStr);
//            baseInfo.setContractNo(baseInfoContractNo.getValueStr());
//            //姓名
//            BaseInfoClientName baseInfoClientName = new BaseInfoClientName();
//            baseInfoClientName.analysisValue(runStr);
//            baseInfo.setClientName(baseInfoClientName.getValueStr());
//        }
//
//        XWPFRun inquiryDateTimeRun = xdocService.getRun(paragraph, new String[]{"查询时间", "证件类型"});
//        if (null != inquiryDateTimeRun) {
//            String runStr = inquiryDateTimeRun.getPictureText();
//            //查询时间
//            BaseInfoInquiryDateTime baseInfoInquiryDateTime = new BaseInfoInquiryDateTime();
//            baseInfoInquiryDateTime.analysisValue(runStr);
//            baseInfo.setInquiryDateTimeStr(baseInfoInquiryDateTime.getValueStr());
//            baseInfo.setInquiryDateTime(baseInfoInquiryDateTime.getValue());
//            //证件类型
//            BaseInfoCardType baseInfoCardType = new BaseInfoCardType();
//            baseInfoCardType.analysisValue(runStr);
//            baseInfo.setCardType(baseInfoCardType.getValueStr());
//        }
//
//        XWPFRun generationDateTimeRun = xdocService.getRun(paragraph, new String[]{"报告时间", "证件号码"});
//        if (null != generationDateTimeRun) {
//            String runStr = generationDateTimeRun.getPictureText();
//            //报告时间
//            BaseInfoGenerationDateTime baseInfoGenerationDateTime = new BaseInfoGenerationDateTime();
//            baseInfoGenerationDateTime.analysisValue(runStr);
//            baseInfo.setGenerationDateTimeStr(baseInfoGenerationDateTime.getValueStr());
//            baseInfo.setGenerationDateTime(baseInfoGenerationDateTime.getValue());
//            //证件号码
//            BaseInfoCardNo baseInfoCardNo = new BaseInfoCardNo();
//            baseInfoCardNo.analysisValue(runStr);
//            baseInfo.setCardNo(baseInfoCardNo.getValueStr());
//            //是否已婚
//            BaseInfoIsMarried baseInfoIsMarried = new BaseInfoIsMarried();
//            baseInfoIsMarried.analysisValue(runStr);
//            baseInfo.setIsMarried(baseInfoIsMarried.getValueStr());
//        }
//        return baseInfo;
    }

    /**
     * 信息概要
     *
     * @param xdoc
     * @date: Created in 2018/4/2 10:45
     * @author: wm
     */
    private SummaryInfo getSummaryInfo(XWPFDocument xdoc) {
        SummaryInfo summaryInfo = new SummaryInfo();

        //资产处置信息解析
        SummaryInfo assetInfo = this.analysisSummaryAssetInfo(xdoc);
        if (null != assetInfo) {
            summaryInfo.setAssetDisposeNum(assetInfo.getAssetDisposeNum());
            summaryInfo.setAssetCompensatoryNum(assetInfo.getAssetCompensatoryNum());
        }

        //段落提取
        XWPFParagraph paragraph = xdocService.getParagraph(xdoc, new String[]{"信用卡", "购房贷款", "其他贷款", "账户数", "未结清/未销户账户数"});
        if (null == paragraph) {
            return null;
        }

        //片段list提取
        List<DocElement> runList = xdocService.getRunList(paragraph);

        for (int i = 0; i < runList.size(); i++) {
            DocElement docElement = runList.get(i);
            if (summaryInfo.getCreditCardAccountNum() == null && summaryInfo.getHouseLoanAccountNum() == null && summaryInfo.getOtherLoanAccountNum() == null
                    && StringUtil.isSimilar("账户数", docElement.getValue(), 6)) {
                if (i + 1 < runList.size()) {
                    String creditCardAccountNum = runList.get(i + 1).getValue();
                    summaryInfo.setCreditCardAccountNum(XdocUtil.getIntegerValue(creditCardAccountNum));
                }
                if (i + 2 < runList.size()) {
                    String houseLoanAccountNum = runList.get(i + 2).getValue();
                    summaryInfo.setHouseLoanAccountNum(XdocUtil.getIntegerValue(houseLoanAccountNum));
                }
                if (i + 3 < runList.size()) {
                    String otherLoanAccountNum = runList.get(i + 3).getValue();
                    summaryInfo.setOtherLoanAccountNum(XdocUtil.getIntegerValue(otherLoanAccountNum));
                }
            }

            if (summaryInfo.getCreditCardUnclearedNum() == null && summaryInfo.getHouseLoanUnclearedNum() == null && summaryInfo.getOtherLoanUnclearedNum() == null
                    && (XdocUtil.fuzzyContains(docElement.getValue(), new SimilarityKeyword("未销户账户数", 60))||XdocUtil.fuzzyContains(docElement.getValue(), new SimilarityKeyword("未结清/未销户账户数", 60)))) {
                if (i + 1 < runList.size()) {
                    String creditCardUnclearedNum = runList.get(i + 1).getValue();
                    summaryInfo.setCreditCardUnclearedNum(XdocUtil.getIntegerValue(creditCardUnclearedNum));
                }
                if (i + 2 < runList.size()) {
                    String houseLoanUnclearedNum = runList.get(i + 2).getValue();
                    summaryInfo.setHouseLoanUnclearedNum(XdocUtil.getIntegerValue(houseLoanUnclearedNum));
                }
                if (i + 3 < runList.size()) {
                    String otherLoanUnclearedNum = runList.get(i + 3).getValue();
                    summaryInfo.setOtherLoanUnclearedNum(XdocUtil.getIntegerValue(otherLoanUnclearedNum));
                }
            }

            if (summaryInfo.getCreditCardOverdueNum() == null && summaryInfo.getHouseLoanOverdueNum() == null && summaryInfo.getOtherLoanOverdueNum() == null
                    && StringUtil.isSimilar("发生过逾期的账户数", docElement.getValue(), 6)) {
                if (i + 1 < runList.size()) {
                    String creditCardOverdueNum = runList.get(i + 1).getValue();
                    summaryInfo.setCreditCardOverdueNum(XdocUtil.getIntegerValue(creditCardOverdueNum));
                }
                if (i + 2 < runList.size()) {
                    String houseLoanOverdueNum = runList.get(i + 2).getValue();
                    summaryInfo.setHouseLoanOverdueNum(XdocUtil.getIntegerValue(houseLoanOverdueNum));
                }
                if (i + 3 < runList.size()) {
                    String otherLoanOverdueNum = runList.get(i + 3).getValue();
                    summaryInfo.setOtherLoanOverdueNum(XdocUtil.getIntegerValue(otherLoanOverdueNum));
                }
            }

            if (summaryInfo.getCreditCardOverdue90DayNum() == null && summaryInfo.getHouseLoanOverdue90DayNum() == null && summaryInfo.getOtherLoanOverdue90DayNum() == null
                    && StringUtil.isSimilar("发生过90天以上逾期的账户数", docElement.getValue(), 6)) {
                if (i + 1 < runList.size()) {
                    String creditCardOverdue90DayNum = runList.get(i + 1).getValue();
                    summaryInfo.setCreditCardOverdue90DayNum(XdocUtil.getIntegerValue(creditCardOverdue90DayNum));
                }
                if (i + 2 < runList.size()) {
                    String houseLoanOverdue90DayNum = runList.get(i + 2).getValue();
                    summaryInfo.setHouseLoanOverdue90DayNum(XdocUtil.getIntegerValue(houseLoanOverdue90DayNum));
                }
                if (i + 3 < runList.size()) {
                    String otherLoanOverdue90DayNum = runList.get(i + 3).getValue();
                    summaryInfo.setOtherLoanOverdue90DayNum(XdocUtil.getIntegerValue(otherLoanOverdue90DayNum));
                }
            }

            if (summaryInfo.getCreditCardGuaranteeNum() == null && summaryInfo.getHouseLoanGuaranteeNum() == null && summaryInfo.getOtherLoanGuaranteeNum() == null
                    && StringUtil.isSimilar("为他人担保笔数", docElement.getValue(), 6)) {
                if (i + 1 < runList.size()) {
                    String creditCardGuaranteeNum = runList.get(i + 1).getValue();
                    summaryInfo.setCreditCardGuaranteeNum(XdocUtil.getIntegerValue(creditCardGuaranteeNum));
                }
                if (i + 2 < runList.size()) {
                    String houseLoanGuaranteeNum = runList.get(i + 2).getValue();
                    summaryInfo.setHouseLoanGuaranteeNum(XdocUtil.getIntegerValue(houseLoanGuaranteeNum));
                }
                if (i + 3 < runList.size()) {
                    String otherLoanGuaranteeNum = runList.get(i + 3).getValue();
                    summaryInfo.setOtherLoanGuaranteeNum(XdocUtil.getIntegerValue(otherLoanGuaranteeNum));
                }
            }
        }
        return summaryInfo;
    }

    /**
     * 资产处置信息
     *
     * @param xdoc
     * @return
     */
    private SummaryInfo analysisSummaryAssetInfo(XWPFDocument xdoc) {
        SummaryInfo assetInfo = new SummaryInfo();
        //段落提取
        XWPFParagraph paragraph = xdocService.getParagraph(xdoc, new String[]{"资产处置信息", "保证人代偿信息", "笔数"});
        if (null == paragraph) {
            return null;
        }

        //片段list提取
        List<DocElement> runList = xdocService.getRunList(paragraph);

        for (int i = 0; i < runList.size(); i++) {
            DocElement docElement = runList.get(i);
            if (assetInfo.getAssetDisposeNum() == null && assetInfo.getAssetCompensatoryNum() == null
                    && StringUtil.isSimilar("笔数", docElement.getValue(), 6)) {
                if (i + 1 < runList.size()) {
                    String assetDisposeNum = runList.get(i + 1).getValue();
                    assetInfo.setAssetDisposeNum(XdocUtil.getIntegerValue(assetDisposeNum));
                }
                if (i + 2 < runList.size()) {
                    String assetCompensatoryNum = runList.get(i + 2).getValue();
                    assetInfo.setAssetCompensatoryNum(XdocUtil.getIntegerValue(assetCompensatoryNum));
                }
            }
        }

        if (assetInfo.getAssetDisposeNum() == null) {
            this.threadCache.addFailInfoList(new FailInfo(SummaryInfo.class.getSimpleName(), "assetDisposeNum", assetInfo.getAssetDisposeNum(), "is null"));
        }
        if (assetInfo.getAssetCompensatoryNum() == null) {
            this.threadCache.addFailInfoList(new FailInfo(SummaryInfo.class.getSimpleName(), "assetCompensatoryNum", assetInfo.getAssetCompensatoryNum(), "is null"));
        }
        return assetInfo;
    }

    @Override
    public InquiryInfo getInquiryInfo(XWPFDocument xdoc) {
        InquiryInfo inquiryInfo = new InquiryInfo();

        List<DocTableCell> docTableCellList = new ArrayList<>();
        docTableCellList.add(new DocTableCell(0, 1));
        docTableCellList.add(new DocTableCell(0, 2));
        docTableCellList.add(new DocTableCell(0, 3));

        List<SimilarityKeyword> similarityKeywordList = new ArrayList<>();
        similarityKeywordList.add(new SimilarityKeyword("查询日期", 66));
        similarityKeywordList.add(new SimilarityKeyword("查询操作员", 66));
        similarityKeywordList.add(new SimilarityKeyword("查询原因", 66));

        //bodyElement的索引
        List<DocElement> authInquiryDEList = xdocService.getTableCellList(xdoc, docTableCellList, new SimilarityKeyword("机构查询记录明细", 70), null);
        Integer authInquiryTableIndex = null;
        if (null != authInquiryDEList && authInquiryDEList.size() > 0) {
            DocElement docElement = xdocService.containsSearch(authInquiryDEList, similarityKeywordList);
            if (null != docElement && docElement.getIndex() != null) {
                IBodyElement bodyElement = xdoc.getBodyElements().get(docElement.getIndex());
                if (bodyElement instanceof XWPFTable) {
                    authInquiryTableIndex = docElement.getIndex();
                    XWPFTable table = (XWPFTable) bodyElement;
                    List<InquiryDetails> inquiryDetails = new ArrayList<>();
                    //机构查询记录 解析
                    DocTableResult<InquiryDetails> docTableResult = this.analysisInquiryDetails(table);
                    if (!CollectionUtils.isEmpty(docTableResult.getList())) {
                        inquiryDetails.addAll(docTableResult.getList());
                    }
                    if (!docTableResult.getIsCombined()){
                        //解析分页后的查询记录
                        List<InquiryDetails> pageAuthInquiryDetailsList = this.analysisPageInquiryDetails(xdoc, docElement.getIndex());
                        if (null != pageAuthInquiryDetailsList) {
                            inquiryDetails.addAll(pageAuthInquiryDetailsList);
                        }
                    }
                    inquiryInfo.setAuthInquiryDetailsList(inquiryDetails);
                }
            }

            if (inquiryInfo.getAuthInquiryDetailsList() == null) {
                this.threadCache.addFailInfoList(new FailInfo(InquiryInfo.class.getSimpleName(), "authInquiryDetailsList", inquiryInfo.getAuthInquiryDetailsList(), "is null"));
            }
        }

//        //bodyElement的索引
//        List<DocElement> personInquiryDEListTemp = xdocService.getTableCellList(xdoc, docTableCellList, new SimilarityKeyword("本人查询记录明细", 70), null);
//        List<DocElement> personInquiryDEList = new ArrayList<>();
//        if (null!=authInquiryTableIndex){
//            //因表格非常相似，所以需要排除企业查询表格
//            for (DocElement docElement:personInquiryDEListTemp){
//                if(docElement.getIndex()>authInquiryTableIndex){
//                    personInquiryDEList.add(docElement);
//                }
//            }
//        }
//        if (null != personInquiryDEList && personInquiryDEList.size() > 0) {
//            DocElement docElement = xdocService.containsSearch(personInquiryDEList, similarityKeywordList);
//            if (null != docElement && docElement.getIndex() != null) {
//                IBodyElement bodyElement = xdoc.getBodyElements().get(docElement.getIndex());
//                if (bodyElement instanceof XWPFTable) {
//                    XWPFTable table = (XWPFTable) bodyElement;
//                    List<InquiryDetails> inquiryDetails = new ArrayList<>();
//                    //机构查询记录 解析
//                    List<InquiryDetails> personInquiryDetailsList = this.analysisInquiryDetails(table);
//                    if (null != personInquiryDetailsList) {
//                        inquiryDetails.addAll(personInquiryDetailsList);
//                    }
//                    List<InquiryDetails> pagePersonInquiryDetailsList = this.analysisPageInquiryDetails(xdoc, docElement.getIndex());
//                    if (null != pagePersonInquiryDetailsList) {
//                        inquiryDetails.addAll(pagePersonInquiryDetailsList);
//                    }
//                    inquiryInfo.setPersonInquiryDetailsList(inquiryDetails);
//                }
//            }
//        }
        return inquiryInfo;
    }

    /**
     * 查询记录 解析docElement对应表格的所有的分页表格解析结果
     *
     * @param xdoc
     * @param bodyElementIndex index是bodyElement索引
     * @return
     */
    private List<InquiryDetails> analysisPageInquiryDetails(XWPFDocument xdoc, int bodyElementIndex) {
        List<InquiryDetails> pageAuthInquiryDetailsList = new ArrayList<>();
        //表格最大支持分3页
        int pageMax = 3;
        //相对表格自身，当前表格是第几页
        int pageNo = 1;
        do {
            //条件1：下一个元素仍是表格
            DocTable docTable = xdocService.getNextTable(xdoc, bodyElementIndex);
            if (docTable == null) {
                break;
            }

            //条件2：下一个表格第一行包含年月则认为是分页的表格
            String cellStr = xdocService.getTableCellStr(docTable.getTable(), new DocTableCell(0, 1));
            if (StringUtil.isEmpty(cellStr)) {
                break;
            }

            boolean isSameTable = XdocUtil.fuzzyContains(cellStr, new SimilarityKeyword("年月", 50));
            if (!isSameTable) {
                break;
            }

            DocTableResult<InquiryDetails> docTableResult = this.analysisInquiryDetails(docTable.getTable());
            if (!CollectionUtils.isEmpty(docTableResult.getList())) {
                pageAuthInquiryDetailsList.addAll(docTableResult.getList());
            }

            if (docTableResult.getIsCombined()){
                return pageAuthInquiryDetailsList;
            }

            bodyElementIndex = docTable.getIndex();
            pageNo++;
        } while (pageNo < pageMax + 1);
        return pageAuthInquiryDetailsList;
    }

    /**
     * 查询记录 解析
     *
     * @param table
     * @return
     * @date: Created in 2018/4/10 16:48
     * @author: wm
     */
    private DocTableResult<InquiryDetails> analysisInquiryDetails(XWPFTable table) {
        DocTableResult<InquiryDetails> docTableResult = new DocTableResult<>();
        List<InquiryDetails> inquiryDetailsList = new ArrayList<>();
        List<XWPFTableRow> rows = table.getRows();
        int startIndex = 1;
        String cellStr = xdocService.getTableCellStr(table, new DocTableCell(0, 1));
        boolean isSameTable = XdocUtil.fuzzyContains(cellStr, new SimilarityKeyword("年月", 50));
        if (isSameTable) {
            startIndex = 0;
        }
        //固定 第二行开始是数据
        for (int i = startIndex; i < rows.size(); i++) {
            XWPFTableRow row = rows.get(i);
            List<XWPFTableCell> cells = row.getTableCells();
            //固定 必须每行必须4列，以及固定每列的字段含义
            if (XdocUtil.fuzzyContains(cells.get(0).getText(), new SimilarityKeyword("本人迕珣记录明细", 66))
                    ||  XdocUtil.fuzzyContains(cells.get(0).getText(), new SimilarityKeyword("编号", 50))){
                docTableResult.setIsCombined(true);
                docTableResult.setList(inquiryDetailsList);
                return docTableResult;
            }
            if (cells.size() == 4) {
                String number = cells.get(0).getText();
                String dateStr = cells.get(1).getText();
                Date date = XdocUtil.getDateValue(dateStr, "yyyy年M月d日");
                String operator = cells.get(2).getText();
                String cause = cells.get(3).getText();
                InquiryDetails authInquiryDetails = new InquiryDetails(number, dateStr, date, operator, cause);
                inquiryDetailsList.add(authInquiryDetails);
            }
        }
        docTableResult.setList(inquiryDetailsList);
        return docTableResult;
    }

    @Override
    public CreditDetail getCreditDetail(XWPFDocument xdoc) {
        CreditDetail creditDetail = new CreditDetail();

        SummaryInfo summaryInfo = this.getSummaryInfo(xdoc);
        creditDetail.setSummaryInfo(summaryInfo);

        //段落范围截取
        List<DocElement> detailDEList = xdocService.getDocElementListBetweenKeywords(xdoc, "为他人担保笔数", "公共记录");
        if (CollectionUtils.isEmpty(detailDEList)){
            detailDEList = xdocService.getDocElementListBetweenKeywords(xdoc, "为他人担保笔数", "系统中没有您");
        }
        if (null == detailDEList || detailDEList.size() == 0) {
            return creditDetail;
        }

        //取得段落索引
        Integer compensationIndex = null;
        Integer creditCardIndex = null;
        Integer homeLoansIndex = null;
        Integer otherLoansIndex = null;
        Integer guaranteeIndex = null;
        Integer blockIndex = null;//控制索引顺序
        for (int i = 0; i < detailDEList.size(); i++) {
            DocElement detailDE = detailDEList.get(i);
            if (StringUtil.isSimilar("保证人代偿信息", detailDE.getValue(), 7)) {
                if (null == compensationIndex) {
                    compensationIndex = i;
                    blockIndex = i;
                }
            }
            if (StringUtil.isSimilar("信用卡", detailDE.getValue(), 7)) {
                if (null == creditCardIndex) {
                    if (null != blockIndex) {
                        if (i > blockIndex) {
                            creditCardIndex = i;
                        }
                    } else {
                        creditCardIndex = i;
                    }
                }
            }
            if (StringUtil.isSimilar("购房贷款", detailDE.getValue(), 7)) {
                if (null == homeLoansIndex) {
                    if (null != blockIndex) {
                        if (i > blockIndex) {
                            homeLoansIndex = i;
                        }
                    } else {
                        homeLoansIndex = i;
                    }
                }
            }
            if (StringUtil.isSimilar("其他贷款", detailDE.getValue(), 7)) {
                if (null == otherLoansIndex) {
                    if (null != blockIndex) {
                        if (i > blockIndex) {
                            otherLoansIndex = i;
                        }
                    } else {
                        otherLoansIndex = i;
                    }
                }
            }
            if (StringUtil.isSimilar("为他人担保信息", detailDE.getValue(), 7)) {
                if (null == guaranteeIndex) {
                    if (null != blockIndex) {
                        if (i > blockIndex) {
                            guaranteeIndex = i;
                        }
                    } else {
                        guaranteeIndex = i;
                    }
                }
            }
        }

        //保证人代偿信息
        if (null != compensationIndex) {
            //固定 段落位置，此段落在最前面
            Integer endIndex = XdocUtil.getFirstNotNullIndex(creditCardIndex, homeLoansIndex, otherLoansIndex, guaranteeIndex);
            endIndex = endIndex == null ? detailDEList.size() : endIndex;
            List<DocElement> docElementList = detailDEList.subList(compensationIndex + 1, endIndex);
            List<Compensatory> compensatoryList = this.analysisCompensatory(docElementList);
            creditDetail.setCompensatoryList(compensatoryList);

            if (compensatoryList == null) {
                this.threadCache.addFailInfoList(new FailInfo(CreditDetail.class.getSimpleName(), "compensatoryList", compensatoryList, "is null"));
            }
        }

        //信用卡
        if (null != creditCardIndex) {
            //固定 段落位置，此段落在最前面
            Integer endIndex = XdocUtil.getFirstNotNullIndex(homeLoansIndex, otherLoansIndex, guaranteeIndex);
            endIndex = endIndex == null ? detailDEList.size() : endIndex;
            List<DocElement> docElementList = detailDEList.subList(creditCardIndex + 1, endIndex);
            CreditCard creditCard = this.analysisCreditCard(docElementList);
            creditDetail.setCreditCard(creditCard);

            if (creditCard == null) {
                this.threadCache.addFailInfoList(new FailInfo(CreditDetail.class.getSimpleName(), "creditCard", creditCard, "is null"));
            }
        }

        //购房贷款
        //· 发生过逾期的账户明细如下：
        //· 从未逾期过的账户明细如下：
        if (null != homeLoansIndex) {
            //固定 段落位置，此段落在最前面
            Integer endIndex = XdocUtil.getFirstNotNullIndex(otherLoansIndex, guaranteeIndex);
            endIndex = endIndex == null ? detailDEList.size() : endIndex;
            List<DocElement> docElementList = detailDEList.subList(homeLoansIndex + 1, endIndex);
            HouseLoan houseLoan = this.analysisHouseLoan(docElementList);
            creditDetail.setHouseLoan(houseLoan);

            if (houseLoan == null) {
                this.threadCache.addFailInfoList(new FailInfo(CreditDetail.class.getSimpleName(), "houseLoan", houseLoan, "is null"));
            }
        }

        //其他贷款
        //· 发生过逾期的账户明细如下：
        //· 从未逾期过的账户明细如下：
        if (null != otherLoansIndex) {
            //固定 段落位置，此段落在最前面
            Integer endIndex = XdocUtil.getFirstNotNullIndex(guaranteeIndex);
            endIndex = endIndex == null ? detailDEList.size() : endIndex;
            List<DocElement> docElementList = detailDEList.subList(otherLoansIndex + 1, endIndex);
            OtherLoan otherLoan = this.analysisOtherLoan(docElementList);
            creditDetail.setOtherLoan(otherLoan);

            if (otherLoan == null) {
                this.threadCache.addFailInfoList(new FailInfo(CreditDetail.class.getSimpleName(), "otherLoan", otherLoan, "is null"));
            }
        }

        //为他人担保信息
        if (null != guaranteeIndex) {
            //固定 段落位置，此段落在最后面
            List<DocElement> docElementList = detailDEList.subList(otherLoansIndex + 1, detailDEList.size());
            List<Warrant> warrantList = this.analysisWarrant(docElementList);
            creditDetail.setWarrantList(warrantList);

            if (warrantList == null) {
                this.threadCache.addFailInfoList(new FailInfo(CreditDetail.class.getSimpleName(), "warrantList", warrantList, "is null"));
            }
        }

        return creditDetail;
    }


    @Override
    public PublicRecord getPublicRecord(XWPFDocument xdoc) {
        PublicRecord publicRecord = null;

        //获取段落 公共记录
        List<DocElement> docElementList = xdocService.getDocElementListBetweenKeywords(xdoc, "公共记录", "查询记录");

        EnforcementRecord enforcementRecord = null;
        if (!CollectionUtils.isEmpty(docElementList)&&docElementList.size()>1){
            //强制执行记录 解析
            enforcementRecord = this.analysisEnforcementRecord(docElementList);
            publicRecord = new PublicRecord();
            publicRecord.setEnforcementRecord(enforcementRecord);
        }

        return publicRecord;
    }

    /**
     * todo wm wait 测试 强制执行记录
     *
     * @param docElementList
     * @return
     */
    private EnforcementRecord analysisEnforcementRecord(List<DocElement> docElementList) {
        EnforcementRecord enforcementRecord = new EnforcementRecord();
        for (DocElement docElement : docElementList) {
            String str = docElement.getValue();

            if (XdocUtil.fuzzyContains(str, new SimilarityKeyword("执行法院：", 66))
                    && enforcementRecord.getCourt() == null && enforcementRecord.getOrderNo() == null) {
                /**执行法院*/
                String court = XdocUtil.substringFuzzyBetween(str, new SimilarityKeyword("执行法院：", 66), new SimilarityKeyword(" 案号", 66));
                enforcementRecord.setCourt(court);
                /**案号*/
                String orderNo = XdocUtil.substringFuzzyAfter(str, new SimilarityKeyword("案号：", 60));
                enforcementRecord.setOrderNo(orderNo);
            } else if (XdocUtil.fuzzyContains(str, new SimilarityKeyword("执行案由：", 66))
                    && enforcementRecord.getExecuteReason() == null && enforcementRecord.getEndType() == null) {
                /**执行案由*/
                String executeReason = XdocUtil.substringFuzzyBetween(str, new SimilarityKeyword("执行案由：", 66), new SimilarityKeyword(" 结案方式", 66));
                enforcementRecord.setExecuteReason(executeReason);
                /**结案方式*/
                String endType = XdocUtil.substringFuzzyAfter(str, new SimilarityKeyword("结案方式:", 66));
                enforcementRecord.setEndType(endType);
            } else if (XdocUtil.fuzzyContains(str, new SimilarityKeyword("立案时间：", 66))
                    && enforcementRecord.getOpenDateStr() == null && enforcementRecord.getStatus() == null) {
                /**立案时间*/
                String openDateStr = XdocUtil.substringFuzzyBetween(str, new SimilarityKeyword("立案时间：", 66), new SimilarityKeyword(" 案件状态", 66));
                DocDate docDate = XdocUtil.analysisPrefixDate(openDateStr, "yyyy年M月");
                if (null != docDate) {
                    enforcementRecord.setOpenDateStr(docDate.getDateStr());
                    enforcementRecord.setOpenDate(docDate.getDate());
                }
                /**案件状态*/
                String status = XdocUtil.substringFuzzyAfter(str, new SimilarityKeyword("案件状态：", 66));
                enforcementRecord.setStatus(status);
            } else if (XdocUtil.fuzzyContains(str, new SimilarityKeyword("申请执行标的：", 66))
                    && enforcementRecord.getApplyExecute() == null && enforcementRecord.getAlreadyExecuted() == null) {
                /**申请执行标的*/
                String applyExecute = XdocUtil.substringFuzzyBetween(str, new SimilarityKeyword("申请执行标的：", 66), new SimilarityKeyword(" 已执行标的", 66));//.replace("--", "");
                enforcementRecord.setApplyExecute(applyExecute);
                /**已执行标的*/
                String alreadyExecuted = XdocUtil.substringFuzzyAfter(str, new SimilarityKeyword("已执行标的：", 66));//.replace("--", "");
                enforcementRecord.setAlreadyExecuted(alreadyExecuted);
            } else if (XdocUtil.fuzzyContains(str, new SimilarityKeyword("申请执行标的金额：", 70))
                    && enforcementRecord.getApplyAmountStr() == null && enforcementRecord.getAlreadyAmountStr() == null) {
                /**申请执行标的金额*/
                String applyAmountStr = XdocUtil.substringFuzzyBetween(str, new SimilarityKeyword("申请执行标的金额：", 70), new SimilarityKeyword(" 已执行标的金额", 70));
                DocMoney applyDocMoney = XdocUtil.analysisPrefixMoney(applyAmountStr);
                if (null != applyDocMoney) {
                    enforcementRecord.setApplyAmountStr(applyDocMoney.getMoneyStr());
                    enforcementRecord.setApplyAmount(applyDocMoney.getMoney());
                }
                /**已执行标的金额*/
                String alreadyAmountStr = XdocUtil.substringFuzzyAfter(str, new SimilarityKeyword("已执行标的金额：", 70));//.replace("--", "");
                DocMoney alreadyDocMoney = XdocUtil.analysisPrefixMoney(alreadyAmountStr);
                if (null != alreadyDocMoney) {
                    enforcementRecord.setAlreadyAmountStr(alreadyDocMoney.getMoneyStr());
                    enforcementRecord.setAlreadyAmount(alreadyDocMoney.getMoney());
                }
            } else if (XdocUtil.fuzzyContains(str, new SimilarityKeyword("结案时间：", 66))
                    && enforcementRecord.getCloseDateStr() == null) {
                /**结案时间*/
                String closeDateStr = XdocUtil.substringFuzzyAfter(str, new SimilarityKeyword("结案时间：", 66));
                DocDate docDate = XdocUtil.analysisPrefixDate(closeDateStr, "yyyy年M月");
                if (null != docDate) {
                    enforcementRecord.setCloseDateStr(docDate.getDateStr());
                    enforcementRecord.setCloseDate(docDate.getDate());
                }
            }
        }
        return enforcementRecord;
    }


    /**
     * 代偿信息解析
     *
     * @param docElementList
     * @return
     */
    private List<Compensatory> analysisCompensatory(List<DocElement> docElementList) {
        List<Compensatory> compensatoryList = new ArrayList<>();
        List<String> strList = xdocService.mergeDocSameLine(docElementList, new XdocCreditParagraphFillter() {
            //固定 出现位置
            @Override
            public boolean isSameBlock(String currentLine) {
                if (StringUtil.isSimilar("从未逾期过的账户明细", currentLine, 7)) {
                    return false;
                }
                return true;
            }
        });

        for (int i = 0; i < strList.size(); i++) {
            String itemStr = strList.get(i);
            Compensatory compensatory = new Compensatory();

            /**代偿日期*/
            if (itemStr.length() > 13) {
                String dateStrTemp = itemStr.substring(0, 13);
                DocDate docDate = XdocUtil.analysisPrefixDate(dateStrTemp);
                compensatory.setDateStr(docDate.getDateStr());
                compensatory.setDate(docDate.getDate());
            }

            /**代偿机构*/
            String organizationTemp = StringUtils.substringBetween(itemStr, "日", "进行");
            if (!StringUtil.isEmpty(organizationTemp)) {
                compensatory.setOrganization(organizationTemp);
            }

            /**代偿金额*/
            String amountStrTemp = StringUtils.substringAfter(itemStr, "金额");
            if (StringUtil.isEmpty(amountStrTemp)) {
                amountStrTemp = StringUtils.substringAfter(itemStr, "，");
            }
            if (!StringUtil.isEmpty(amountStrTemp)) {
                DocMoney docMoney = XdocUtil.analysisPrefixMoney(amountStrTemp);
                compensatory.setAmountStr(docMoney.getMoneyStr());
                compensatory.setAmount(docMoney.getMoney());
            }

            /**最近一次还款日期*/
            String latestRepaymentDateStrTemp = XdocUtil.substringBetween(itemStr, new String[]{"日期为", "期为", "款日期", "还款日", "次还款"}, new String[]{"，余额", "，余", "，"});
            if (!StringUtil.isEmpty(latestRepaymentDateStrTemp)) {
                DocDate docDate = XdocUtil.analysisPrefixDate(latestRepaymentDateStrTemp);
                compensatory.setLatestRepaymentDateStr(docDate.getDateStr());
                compensatory.setLatestRepaymentDate(docDate.getDate());

                if (compensatory.getLatestRepaymentDate() == null) {
                    this.threadCache.addFailInfoList(new FailInfo(Compensatory.class.getSimpleName(), i+".latestRepaymentDate", docDate.getDate(), "is null"));
                }
            }

            /**余额*/
            String amountRemainStrTemp = StringUtils.substringAfter(itemStr, "金额");
            if (!StringUtil.isEmpty(amountRemainStrTemp)) {
                DocMoney docMoney = XdocUtil.analysisPrefixMoney(amountRemainStrTemp);
                compensatory.setAmountRemainStr(docMoney.getMoneyStr());
                compensatory.setAmountRemain(docMoney.getMoney());
            }
            compensatoryList.add(compensatory);
        }
        return compensatoryList;
    }

    /**
     * 为他人担保信息
     * 包含贷款担保和信用卡担保
     *
     * @param docElementList
     * @return
     */
    private List<Warrant> analysisWarrant(List<DocElement> docElementList) {
        List<Warrant> warrantList = new ArrayList<>();
        List<String> strList = xdocService.getDocListByTitle(docElementList, "为他人担保信息", new XdocCreditParagraphFillter() {
        });

        for (int i = 0; i < strList.size(); i++) {
            String itemStr = strList.get(i);
            itemStr = StringUtils.replaceAll(itemStr, "：", ":");
            Warrant warrant = new Warrant();

            /**日期*/
            if (itemStr.length() > 13) {
                String dateStrTemp = itemStr.substring(0, 13);
                DocDate docDate = XdocUtil.analysisPrefixDate(dateStrTemp);
                warrant.setStartDateStr(docDate.getDateStr());
                warrant.setStartDate(docDate.getDate());
            }

            /**被担保人姓名*/
            String voucheeName = XdocUtil.substringBetween(itemStr, new String[]{"，为", "为"}, new String[]{"（证件", "（证", "（", "证件"});
            warrant.setVoucheeName(voucheeName);

            /**证件类型*/
            String cardType = XdocUtil.substringBetween(itemStr, new String[]{"证件类型:", "类型:", "类型"}, new String[]{"，证件号码", "，证件号", "，证件"});
            warrant.setCardType(cardType);

            /**证件号码*/
            String cardNo = XdocUtil.substringBetween(itemStr, new String[]{"证件号码:", "号码:", "号码"}, new String[]{"）在", "）", "在"});
            warrant.setCardNo(cardNo);

            /**签约机构 distributionAuthority*/
            String signOrganization = XdocUtil.substringBetween(itemStr, new String[]{"）在", "在"}, new String[]{"办理的", "办理", "办"});
            warrant.setSignOrganization(signOrganization);

            /**担保类型 creditType*/
            String warrantType = XdocUtil.substringBetween(itemStr, new String[]{"办理的", "理的"}, new String[]{"，担保", "，担", "，"});
            warrant.setWarrantType(warrantType);

            /**合同金额*/
            //信用卡   StringUtils.substringBetween(w, "担保信用卡授信额度", "，担保金额")
            String contractAmountStr = XdocUtil.substringBetween(itemStr, new String[]{"合同金额", "合同金"}, new String[]{"担保金", "担保"});
            if (!StringUtil.isEmpty(contractAmountStr)) {
                DocMoney docMoney = XdocUtil.analysisPrefixMoney(contractAmountStr);
                warrant.setContractAmountStr(docMoney.getMoneyStr());
                warrant.setContractAmount(docMoney.getMoney());
            }

            /**担保金额*/
            String warrantAmountStr = XdocUtil.substringBetween(itemStr, new String[]{"担保金", "保金"}, new String[]{"。", "截至"});
            if (!StringUtil.isEmpty(warrantAmountStr)) {
                DocMoney docMoney = XdocUtil.analysisPrefixMoney(warrantAmountStr);
                warrant.setWarrantAmountStr(docMoney.getMoneyStr());
                warrant.setWarrantAmount(docMoney.getMoney());
            }

            /**最新更新日期*/
            String recentUpdateDateStr = XdocUtil.substringBetween(itemStr, new String[]{"截至", "至", "截"}, new String[]{"，"});
            DocDate docDate = XdocUtil.analysisPrefixDate(recentUpdateDateStr);
            warrant.setRecentUpdateDateStr(docDate.getDateStr());
            warrant.setRecentUpdateDate(docDate.getDate());


            /**担保贷款余额、担保信用卡已用额度*/
            //信用卡   StringUtils.substringAfter(w, "，担保信用卡已用额度")
            String warrantRemainAmountStr = XdocUtil.substringAfter(itemStr, new String[]{"贷款余额", "款余额", "款余"});
            DocMoney docMoney = XdocUtil.analysisPrefixMoney(warrantRemainAmountStr);
            warrant.setWarrantRemainAmountStr(docMoney.getMoneyStr());
            warrant.setWarrantRemainAmount(docMoney.getMoney());

            if (null == warrant.getRecentUpdateDate()) {
                this.threadCache.addFailInfoList(new FailInfo(Warrant.class.getSimpleName(), i+".recentUpdateDate", warrant.getRecentUpdateDate(), "is null"));
            }
            if (null == warrant.getWarrantRemainAmount()) {
                this.threadCache.addFailInfoList(new FailInfo(Warrant.class.getSimpleName(), i+".warrantRemainAmount", warrant.getWarrantRemainAmount(), "is null"));
            }
            warrantList.add(warrant);
        }
        return warrantList;
    }

    /**
     * 购房贷款解析
     *
     * @param docElementList
     * @return
     */
    private HouseLoan analysisHouseLoan(List<DocElement> docElementList) {
        HouseLoan houseLoan = new HouseLoan();
        List<String> overdueLoanStrList = xdocService.getDocListByTitle(docElementList, "发生过逾期的账户明细如下", new XdocCreditParagraphFillter() {
            //固定 出现位置
            @Override
            public boolean isSameBlock(String currentLine) {
                if (StringUtil.isSimilar("从未逾期过的账户明细", currentLine, 7)) {
                    return false;
                }
                return true;
            }
        });
        List<Loan> overdueLoanList = this.analysisLoanDetailList(overdueLoanStrList, "houseLoan.overdueLoanList");
        houseLoan.setOverdueLoanList(overdueLoanList);


        List<String> unOverdueLoanStrList = xdocService.getDocListByTitle(docElementList, "从未逾期过的账户明细如下", new XdocCreditParagraphFillter() {
        });
        List<Loan> unOverdueLoanList = this.analysisLoanDetailList(unOverdueLoanStrList, "houseLoan.unOverdueLoanList");
        houseLoan.setUnOverdueLoanList(unOverdueLoanList);


        if (!CollectionUtils.isEmpty(overdueLoanStrList) && CollectionUtils.isEmpty(overdueLoanList)) {
            this.threadCache.addFailInfoList(new FailInfo(HouseLoan.class.getSimpleName(), "overdueLoanList", overdueLoanList, "isEmpty"));
        }
        if (!CollectionUtils.isEmpty(unOverdueLoanStrList) && CollectionUtils.isEmpty(unOverdueLoanList)) {
            this.threadCache.addFailInfoList(new FailInfo(HouseLoan.class.getSimpleName(), "unOverdueLoanList", unOverdueLoanList, "isEmpty"));
        }
        return houseLoan;
    }

    /**
     * 其他贷款解析
     *
     * @param docElementList
     * @return
     */
    private OtherLoan analysisOtherLoan(List<DocElement> docElementList) {
        OtherLoan otherLoan = new OtherLoan();
        List<String> overdueLoanStrList = xdocService.getDocListByTitle(docElementList, "发生过逾期的账户明细如下", new XdocCreditParagraphFillter() {
            //固定 出现位置
            @Override
            public boolean isSameBlock(String currentLine) {
                if (StringUtil.isSimilar("从未逾期过的账户明细", currentLine, 7)) {
                    return false;
                }
                return true;
            }
        });
        List<Loan> overdueLoanList = this.analysisLoanDetailList(overdueLoanStrList, "otherLoan.overdueLoanList");
        otherLoan.setOverdueLoanList(overdueLoanList);


        List<String> unOverdueLoanStrList = xdocService.getDocListByTitle(docElementList, "从未逾期过的账户明细如下", new XdocCreditParagraphFillter() {
        });
        List<Loan> unOverdueLoanList = this.analysisLoanDetailList(unOverdueLoanStrList, "otherLoan.unOverdueLoanList");
        otherLoan.setUnOverdueLoanList(unOverdueLoanList);


        if (!CollectionUtils.isEmpty(overdueLoanStrList) && CollectionUtils.isEmpty(overdueLoanList)) {
            this.threadCache.addFailInfoList(new FailInfo(HouseLoan.class.getSimpleName(), "overdueLoanList", overdueLoanList, "isEmpty"));
        }
        if (!CollectionUtils.isEmpty(unOverdueLoanStrList) && CollectionUtils.isEmpty(unOverdueLoanList)) {
            this.threadCache.addFailInfoList(new FailInfo(HouseLoan.class.getSimpleName(), "unOverdueLoanList", unOverdueLoanList, "isEmpty"));
        }
        return otherLoan;
    }

    /**
     * 贷款明细列表逐条解析
     *
     * @param loanStrList
     * @return
     */
    private List<Loan> analysisLoanDetailList(List<String> loanStrList, String className) {
        if (null==loanStrList){
            return null;
        }
        List<Loan> loanList = new ArrayList<>();
        //        //循环解析每一条数据
        for (int i = 0; i < loanStrList.size(); i++) {
            String item = loanStrList.get(i);
            item = StringUtils.deleteWhitespace(item);
            Loan loan = new Loan();

            BaseCredit baseCredit = this.analysisBaseCredit(item);
            BeanUtils.copyProperties(baseCredit, loan);

            /**到期日期*/
            String expireDateStrTemp = StringUtils.substringBetween(item, "，", "到期");
            if (!StringUtil.isEmpty(expireDateStrTemp)) {
                Date date = XdocUtil.getDateValue(expireDateStrTemp, "yyyy年M月d日");
                loan.setExpireDateStr(expireDateStrTemp);
                loan.setExpireDate(date);

                if (null == date) {
                    this.threadCache.addFailInfoList(new FailInfo(className, i + ".expireDate", date, "is null"));
                }
            }

            /**贷款余额*/
            String loanBalanceStrTemp = StringUtils.substringAfter(item, "余额");
            if (!StringUtil.isEmpty(loanBalanceStrTemp)) {
                DocMoney docMoney = XdocUtil.analysisPrefixMoney(loanBalanceStrTemp);
                loan.setLoanBalanceStr(docMoney.getMoneyStr());
                loan.setLoanBalance(docMoney.getMoney());

                if (null == loan.getLoanBalance()) {
                    this.threadCache.addFailInfoList(new FailInfo(className, i + ".loanBalance", loan.getLoanBalance(), "is null"));
                }
            } else {
                loanBalanceStrTemp = StringUtils.substringAfter(item, "截至");
                if (!StringUtil.isEmpty(loanBalanceStrTemp)) {
                    loanBalanceStrTemp = StringUtils.substringBetween(loanBalanceStrTemp, "，", "。");
                    if (!StringUtil.isEmpty(loanBalanceStrTemp)) {
                        DocMoney docMoney = XdocUtil.analysisPrefixMoney(loanBalanceStrTemp);
                        loan.setLoanBalanceStr(docMoney.getMoneyStr());
                        loan.setLoanBalance(docMoney.getMoney());
                    }

                    if (null == loan.getLoanBalance()) {
                        this.threadCache.addFailInfoList(new FailInfo(className, i + ".loanBalance", loan.getLoanBalance(), "is null"));
                    }
                }
            }

            /**结清日期*/
            String closeDateStrTemp = XdocUtil.substringBefore(item, "已结清");
            if (!StringUtil.isEmpty(closeDateStrTemp) && closeDateStrTemp.length() > 9) {
                closeDateStrTemp = closeDateStrTemp.substring(closeDateStrTemp.length() - 9);
                DocDate docDate = XdocUtil.analysisPrefixDate(closeDateStrTemp, "yyyy年M月");
                loan.setCloseDateStr(docDate.getDateStr());
                loan.setCloseDate(docDate.getDate());

                if (null == loan.getCloseDate()) {
                    this.threadCache.addFailInfoList(new FailInfo(className, i + ".closeDate", loan.getCloseDate(), "is null"));
                }
            }


            /**逾期金额*/
            String overdueAmountStrTemp = StringUtils.substringAfter(item, "逾期金额");
            if (!StringUtil.isEmpty(overdueAmountStrTemp)) {
                DocMoney docMoney = XdocUtil.analysisPrefixMoney(overdueAmountStrTemp);
                loan.setOverdueAmountStr(docMoney.getMoneyStr());
                loan.setOverdueAmount(docMoney.getMoney());

                if (null == loan.getOverdueAmount()) {
                    this.threadCache.addFailInfoList(new FailInfo(className, i + ".overdueAmount", loan.getOverdueAmount(), "is null"));
                }
            }

            //5年内逾期几个月处于逾期状态
            Integer recent5YearsOverdueNumOfMonth = this.analysisFieldRecent5YearsOverdueNumOfMonth(item);
            loan.setRecent5YearsOverdueNumOfMonth(recent5YearsOverdueNumOfMonth);

            //其中几个月逾期超过90天
            Integer recent5YearsOverdue90DaysNumOfMonth = this.analysisFieldRecent5YearsOverdue90DaysNumOfMonth(item);
            loan.setRecent5YearsOverdue90DaysNumOfMonth(recent5YearsOverdue90DaysNumOfMonth);


            if (null == recent5YearsOverdueNumOfMonth) {
                this.threadCache.addFailInfoList(new FailInfo(className, i + ".recent5YearsOverdueNumOfMonth", null, "is null"));
            }
            if (null == recent5YearsOverdue90DaysNumOfMonth) {
                this.threadCache.addFailInfoList(new FailInfo(className, i + ".recent5YearsOverdue90DaysNumOfMonth", null, "is null"));
            }
            loanList.add(loan);
        }
        return loanList;
    }

    /**
     * 逾期几个月解析
     *
     * @param itemStr 每条记录的str
     * @return 解析失败返回null，无逾期则返回0
     */
    private Integer analysisFieldRecent5YearsOverdueNumOfMonth(String itemStr) {
        Integer recent5YearsOverdueNumOfMonth = null;
        String recent5YearsOverdueNumOfMonthTemp = XdocUtil.substringBefore(itemStr, "逾期状态");
        if (!StringUtil.isEmpty(recent5YearsOverdueNumOfMonthTemp)) {
            if (recent5YearsOverdueNumOfMonthTemp.length() > 7) {
                recent5YearsOverdueNumOfMonthTemp = recent5YearsOverdueNumOfMonthTemp.substring(recent5YearsOverdueNumOfMonthTemp.length() - 7);
                String recent5YearsOverdueNumOfMonthStr = XdocUtil.analysisSuffixInteger(recent5YearsOverdueNumOfMonthTemp);
                recent5YearsOverdueNumOfMonth = XdocUtil.getIntegerValue(recent5YearsOverdueNumOfMonthStr);
            }
        }
        if (null == recent5YearsOverdueNumOfMonth) {
            //逾期状态
            if (!itemStr.contains("期状") && !itemStr.contains("处于") && !itemStr.contains("内有")) {
                recent5YearsOverdueNumOfMonth = 0;
            }
        }
        return recent5YearsOverdueNumOfMonth;
    }

    /**
     * 解析 其中几个月逾期超过90天
     *
     * @param itemStr 每条记录的str
     * @return 解析失败返回null，无逾期则返回0
     */
    private Integer analysisFieldRecent5YearsOverdue90DaysNumOfMonth(String itemStr) {
        Integer recent5YearsOverdue90DaysNumOfMonth = null;
        String recent5YearsOverdue90DaysNumOfMonthTemp = StringUtils.substringBetween(itemStr, "其中", "超过90");
        if (!StringUtil.isEmpty(recent5YearsOverdue90DaysNumOfMonthTemp)) {
            String recent5YearsOverdue90DaysNumOfMonthStr = XdocUtil.analysisPrefixInteger(recent5YearsOverdue90DaysNumOfMonthTemp);
            recent5YearsOverdue90DaysNumOfMonth = XdocUtil.getIntegerValue(recent5YearsOverdue90DaysNumOfMonthStr);
        }
        if (null == recent5YearsOverdue90DaysNumOfMonth) {
            if (itemStr.contains("过90") || itemStr.contains("90天") || !itemStr.contains("过90") || !itemStr.contains("90天")) {
                recent5YearsOverdue90DaysNumOfMonth = 0;
            }
        }
        return recent5YearsOverdue90DaysNumOfMonth;
    }

    /**
     * 信用卡明细列表解析
     *
     * @param docElementList
     * @return
     */
    private CreditCard analysisCreditCard(List<DocElement> docElementList) {
        CreditCard creditCard = new CreditCard();
        //· 发生过逾期的贷记卡账户明细如下：
        List<String> creditCardOverdueDetailsStrList = xdocService.getDocListByTitle(docElementList, "发生过逾期的贷记卡账户明细如下", new XdocCreditParagraphFillter() {
            //固定 出现位置
            @Override
            public boolean isSameBlock(String currentLine) {
                if (StringUtil.isSimilar("透支超过60天的准贷记卡账户明细如下", currentLine, 7)
                        || StringUtil.isSimilar("从未逾期过的贷记卡及透支未超过60天的准贷记卡账户明细如下", currentLine, 7)) {
                    return false;
                }
                return true;
            }
        });
        if (!CollectionUtils.isEmpty(creditCardOverdueDetailsStrList)) {
            List<CreditCardOverdueDetails> creditCardOverdueDetailsList = new ArrayList<>();
            //循环解析每一条数据
            for (int i = 0; i < creditCardOverdueDetailsStrList.size(); i++) {
                String item = creditCardOverdueDetailsStrList.get(i);
                item = StringUtils.deleteWhitespace(item);
                CreditCardOverdueDetails creditCardOverdueDetails = this.analysisCreditCardOverdueDetails(item, i);
                creditCardOverdueDetailsList.add(creditCardOverdueDetails);
            }
            creditCard.setCreditCardOverdueDetailsList(creditCardOverdueDetailsList);

            if (!CollectionUtils.isEmpty(creditCardOverdueDetailsStrList) && CollectionUtils.isEmpty(creditCardOverdueDetailsList)) {
                this.threadCache.addFailInfoList(new FailInfo(CreditCard.class.getSimpleName(), "creditCardOverdueDetailsList", null, "isEmpty"));
            }
        }


        //· 透支超过60天的准贷记卡账户明细如下：
        List<String> overdraft60DaysDetailsStrList = xdocService.getDocListByTitle(docElementList, "透支超过60天的准贷记卡账户明细如下", new XdocCreditParagraphFillter() {
            //固定 出现位置
            @Override
            public boolean isSameBlock(String currentLine) {
                if (StringUtil.isSimilar("从未逾期过的贷记卡及透支未超过60天的准贷记卡账户明细如下", currentLine, 7)) {
                    return false;
                }
                return true;
            }
        });
        if (!CollectionUtils.isEmpty(overdraft60DaysDetailsStrList)) {
            List<Overdraft60DaysDetails> overdraft60DaysDetailsList = new ArrayList<>();
            //循环解析每一条数据
            for (int i = 0; i < overdraft60DaysDetailsStrList.size(); i++) {
                String item = overdraft60DaysDetailsStrList.get(i);
                item = StringUtils.deleteWhitespace(item);
                Overdraft60DaysDetails overdraft60DaysDetails = this.analysisOverdraft60DaysDetails(item);
                overdraft60DaysDetailsList.add(overdraft60DaysDetails);
            }
            creditCard.setOverdraft60DaysDetailsList(overdraft60DaysDetailsList);
            if (!CollectionUtils.isEmpty(overdraft60DaysDetailsStrList) && CollectionUtils.isEmpty(overdraft60DaysDetailsList)) {
                this.threadCache.addFailInfoList(new FailInfo(CreditCard.class.getSimpleName(), "overdraft60DaysDetailsList", null, "isEmpty"));
            }
        }

        //· 从未逾期过的贷记卡及透支未超过60天的准贷记卡账户明细如下：
        List<String> UnOverdueAndUnOverdraft60DaysDetailsStrList = xdocService.getDocListByTitle(docElementList, "从未逾期过的贷记卡及透支未超过60天的准贷记卡账户明细如下", new XdocCreditParagraphFillter() {
        });
        if (!CollectionUtils.isEmpty(UnOverdueAndUnOverdraft60DaysDetailsStrList)) {
            List<BaseCreditCard> unOverdueAndUnOverdraft60DaysDetailsList = new ArrayList<>();
            //循环解析每一条数据
            for (int i = 0; i < UnOverdueAndUnOverdraft60DaysDetailsStrList.size(); i++) {
                String item = UnOverdueAndUnOverdraft60DaysDetailsStrList.get(i);
                item = StringUtils.deleteWhitespace(item);
                BaseCreditCard unOverdueAndUnOverdraft60DaysDetails = this.analysisBaseCreditCard(item, CreditCard.class.getSimpleName() + "." + i);
                unOverdueAndUnOverdraft60DaysDetailsList.add(unOverdueAndUnOverdraft60DaysDetails);
            }
            creditCard.setUnOverdueAndUnOverdraft60DaysDetailsList(unOverdueAndUnOverdraft60DaysDetailsList);
            if (!CollectionUtils.isEmpty(UnOverdueAndUnOverdraft60DaysDetailsStrList) && CollectionUtils.isEmpty(unOverdueAndUnOverdraft60DaysDetailsList)) {
                this.threadCache.addFailInfoList(new FailInfo(CreditCard.class.getSimpleName(), "unOverdueAndUnOverdraft60DaysDetailsList", null, "isEmpty"));
            }
        }
        if (CollectionUtils.isEmpty(creditCard.getCreditCardOverdueDetailsList())
                && CollectionUtils.isEmpty(creditCard.getOverdraft60DaysDetailsList())
                && CollectionUtils.isEmpty(creditCard.getUnOverdueAndUnOverdraft60DaysDetailsList())) {
            return null;
        }
        return creditCard;
    }

    /**
     * 透支超过60天的准贷记卡账户明细
     *
     * @param itemStr
     * @return
     */
    private Overdraft60DaysDetails analysisOverdraft60DaysDetails(String itemStr) {
        Overdraft60DaysDetails details = new Overdraft60DaysDetails();
        BaseCredit baseCredit = this.analysisBaseCredit(itemStr);
        BeanUtils.copyProperties(baseCredit, details);

        //透支余额
        String overdraftBalanceStrTemp = StringUtils.substringAfter(itemStr, "透支余额");
        DocMoney overdraftBalanceDocMoney = XdocUtil.analysisPrefixMoney(overdraftBalanceStrTemp);
        details.setOverdraftBalanceStr(overdraftBalanceDocMoney.getMoneyStr());
        details.setOverdraftBalance(overdraftBalanceDocMoney.getMoney());

        //5年内几个月透支超过60天

        Integer recent5YearsOverdraft60DaysNumOfMonth = null;
        String recent5YearsOverdraft60DaysNumOfMonthStrTemp = StringUtils.substringAfter(itemStr, "年内有");
        if (!StringUtil.isEmpty(recent5YearsOverdraft60DaysNumOfMonthStrTemp)) {
            if (recent5YearsOverdraft60DaysNumOfMonthStrTemp.length() > 3) {
                recent5YearsOverdraft60DaysNumOfMonthStrTemp = recent5YearsOverdraft60DaysNumOfMonthStrTemp.substring(0, 3);
            }
            String recent5YearsOverdueNumOfMonthStr = XdocUtil.analysisSuffixInteger(recent5YearsOverdraft60DaysNumOfMonthStrTemp);
            recent5YearsOverdraft60DaysNumOfMonth = XdocUtil.getIntegerValue(recent5YearsOverdueNumOfMonthStr);
        }
        if (null == recent5YearsOverdraft60DaysNumOfMonth) {
            //逾期状态
            if (!itemStr.contains("年内有") && !itemStr.contains("内有")) {
                recent5YearsOverdraft60DaysNumOfMonth = 0;
            }
        }
        details.setRecent5YearsOverdraft60DaysNumOfMonth(recent5YearsOverdraft60DaysNumOfMonth);

        //其中几个月透支超过90天
        Integer recent5YearsOverdraft90DaysNumOfMonth = null;
        String recent5YearsOverdraft90DaysNumOfMonthStrTemp = StringUtils.substringBetween(itemStr, "其中", "超过90");
        if (!StringUtil.isEmpty(recent5YearsOverdraft90DaysNumOfMonthStrTemp)) {
            String recent5YearsOverdue90DaysNumOfMonthStr = XdocUtil.analysisPrefixInteger(recent5YearsOverdraft90DaysNumOfMonthStrTemp);
            recent5YearsOverdraft90DaysNumOfMonth = XdocUtil.getIntegerValue(recent5YearsOverdue90DaysNumOfMonthStr);
        }
        if (null == recent5YearsOverdraft90DaysNumOfMonth) {
            if (itemStr.contains("过90") || itemStr.contains("90天") || !itemStr.contains("过90") || !itemStr.contains("90天")) {
                recent5YearsOverdraft90DaysNumOfMonth = 0;
            }
        }
        details.setRecent5YearsOverdraft90DaysNumOfMonth(recent5YearsOverdraft90DaysNumOfMonth);

        return details;
    }

    /**
     * 发生过逾期的贷记卡账户明细
     *
     * @param itemStr
     * @return
     */
    private CreditCardOverdueDetails analysisCreditCardOverdueDetails(String itemStr, int i) {
        CreditCardOverdueDetails details = new CreditCardOverdueDetails();
        BaseCreditCard baseCreditCard = this.analysisBaseCreditCard(itemStr, CreditCardOverdueDetails.class.getSimpleName()+"."+i);
        BeanUtils.copyProperties(baseCreditCard, details);

        //5年内逾期几个月处于逾期状态
        Integer recent5YearsOverdueNumOfMonth = this.analysisFieldRecent5YearsOverdueNumOfMonth(itemStr);
        details.setRecent5YearsOverdueNumOfMonth(recent5YearsOverdueNumOfMonth);

        //其中几个月逾期超过90天
        Integer recent5YearsOverdue90DaysNumOfMonth = this.analysisFieldRecent5YearsOverdue90DaysNumOfMonth(itemStr);
        details.setRecent5YearsOverdue90DaysNumOfMonth(recent5YearsOverdue90DaysNumOfMonth);

        return details;
    }


    @SneakyThrows
    private BaseCreditCard analysisBaseCreditCard(String itemStr, String className) {
        BaseCreditCard baseCreditCard = new BaseCreditCard();
        BaseCredit baseCredit = this.analysisBaseCredit(itemStr);
        BeanUtils.copyProperties(baseCredit, baseCreditCard);

        //信用卡已使用额度
        String creditCardUsedAmountStrTemp = XdocUtil.substringFuzzyAfter(itemStr, new SimilarityKeyword("使用额度", 66));
        if (!StringUtil.isEmpty(creditCardUsedAmountStrTemp)) {
            //取得最前面的数字
            DocMoney docIntMoney = XdocUtil.analysisPrefixMoney(creditCardUsedAmountStrTemp);
            baseCreditCard.setCreditCardUsedAmountStr(docIntMoney.getMoneyStr());
            baseCreditCard.setCreditCardUsedAmount(docIntMoney.getMoney());

            if (null == baseCreditCard.getCreditCardUsedAmount()) {
                this.threadCache.addFailInfoList(new FailInfo(className, "creditCardUsedAmount", null, "is null"));
            }
        }
        return baseCreditCard;
    }

    private BaseCredit analysisBaseCredit(String itemStr) {
        BaseCredit baseCredit = new BaseCredit();
        //发放日期
        if (itemStr.length() > 13) {
            String cardDistributionDateStrTemp = itemStr.substring(0, 13);
            DocDate docDate = XdocUtil.analysisPrefixDate(cardDistributionDateStrTemp);
            baseCredit.setDistributionDateStr(docDate.getDateStr());
            baseCredit.setDistributionDate(docDate.getDate());
        }

        //发卡机构名称
        //固定"中国"开头 “发放的”结尾
        String distributionAuthority = StringUtils.substringBetween(itemStr, "中国", "发放的");
        if (!StringUtil.isEmpty(distributionAuthority)) {
            distributionAuthority = "中国".concat(distributionAuthority);
        }
        if (StringUtil.isEmpty(distributionAuthority)) {
            distributionAuthority = StringUtils.substringBetween(itemStr, "日", "发放的");
        }
        baseCredit.setDistributionAuthority(distributionAuthority);


        //信用类型
        String distributionItemName = StringUtils.substringBetween(itemStr, "发放的", "）");
        if (!StringUtil.isEmpty(distributionItemName) && distributionItemName.length() < 13) {
            if (StringUtils.isNumeric(distributionItemName.substring(0, 1))) {
                //若为数字，则itemStr是贷款类型，此区间字符串是额度
                DocMoney money = XdocUtil.analysisPrefixMoney(distributionItemName);
                baseCredit.setAmountStr(money.getMoneyStr());
                baseCredit.setAmount(money.getMoney());

                String creditTypeTemp = StringUtils.substringBetween(itemStr, "）", "，");
                if (!StringUtil.isEmpty(creditTypeTemp)) {
                    String creditType = creditTypeTemp;
                    baseCredit.setCreditType(creditType);
                }
            } else {
                //若非数字，则itemStr是信用卡，此区间字符串是卡片类型
                baseCredit.setCreditType(distributionItemName.concat("）"));
            }
        }

        //最近更新时间
        //固定 区间
        String recentUpdateDateStr = StringUtils.substringBetween(itemStr, "截至", "，");
        DocDate docDate = XdocUtil.analysisPrefixDate(recentUpdateDateStr, "yyyy年M月");
        baseCredit.setRecentUpdateDateStr(docDate.getDateStr());
        baseCredit.setRecentUpdateDate(docDate.getDate());

        //信用额度
        String creditCardTotalAmountStrTemp = StringUtils.substringAfter(itemStr, "额度");
        if (!StringUtil.isEmpty(creditCardTotalAmountStrTemp) && creditCardTotalAmountStrTemp.length() > 16) {
            creditCardTotalAmountStrTemp = creditCardTotalAmountStrTemp.substring(0, 16);
            DocMoney docIntMoney = XdocUtil.analysisPrefixMoney(creditCardTotalAmountStrTemp);
            baseCredit.setAmountStr(docIntMoney.getMoneyStr());
            baseCredit.setAmount(docIntMoney.getMoney());
        }
        return baseCredit;
    }
}
