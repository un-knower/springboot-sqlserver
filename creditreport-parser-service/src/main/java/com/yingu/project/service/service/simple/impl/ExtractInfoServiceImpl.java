package com.yingu.project.service.service.simple.impl;

import com.yingu.project.service.service.simple.ExtractInfoService;
import com.yingu.project.service.service.simple.WordService;
import lombok.SneakyThrows;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 正常匹配
 * @Date: Created in 2018/3/26 10:40
 * @Author: wm
 */
@Service
@Slf4j
@Scope(value = "prototype")
public class ExtractInfoServiceImpl implements ExtractInfoService {

    private final static String regexForDate = "^.*[0-9]{4}[\\u4e00-\\u9fa5][0-9]{1,2}[\\u4e00-\\u9fa5][0-9]{1,2}.+$";

    static ThreadLocal<DateFormat> inputSdf = new ThreadLocal<DateFormat>(){
        @Override
        protected SimpleDateFormat initialValue(){
            return new SimpleDateFormat("yyyy.MM.ddHH:mm:ss");
        }
    };

    static ThreadLocal<DateFormat> outputSdf = new ThreadLocal<DateFormat>(){
        @Override
        protected SimpleDateFormat initialValue(){
            return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        }
    };

    @Override
    @SneakyThrows(ParseException.class)
    public void getBaseInfo(String text, Map<String, Object> resultMap) {

//        var inputFormat = "yyyy.MM.ddHH:mm:ss";
//        var inputSdf = new SimpleDateFormat(inputFormat);
//        var outputFormat = "yyyy-MM-dd'T'HH:mm:ss";
//        var outputSdf = new SimpleDateFormat(outputFormat);

        //获取头部信息，将其去掉空格，根据关键字取其中的值
        var header = "报告编号:" + StringUtils.substringBetween(text, "报告编号:", "信贷记录");


        header = StringUtils.deleteWhitespace(header);
        var isMarried = StringUtils.substring(header, header.length() - 2);
        header = StringUtils.removeEnd(header, isMarried);
        var contractNo = StringUtils.substringBetween(header, "报告编号:", "查询时间:");

        var inquiryDateTime = StringUtils.substringBetween(header, "查询时间:", "报告时间:");
        var inquiryDateTimeObj = inputSdf.get().parse(inquiryDateTime);
        var generationDateTime = StringUtils.substringBetween(header, "报告时间:", "姓名:");
        var generationDateTimeObj = inputSdf.get().parse(generationDateTime);

        var clientName = StringUtils.substringBetween(header, "姓名:", "证件类型:");
        var cardType = StringUtils.substringBetween(header, "证件类型:", "证件号码:");
        var cardNo = StringUtils.substringAfter(header, "证件号码:");
        var creditInfo = StringUtils.substringBetween(text,"信息概要","信用卡");
        var assetsDispositionInfo = "";
        if(StringUtils.contains(creditInfo,"资产处置信息")){
            var creditInfoArray = StringUtils.split(creditInfo,System.lineSeparator());
            var creditInfoList = Arrays.asList(creditInfoArray);
            var creditAmountArray = StringUtils.split(creditInfoList.get(2)," ");
            var creditAmountList = Arrays.asList(creditAmountArray);
            assetsDispositionInfo =  creditAmountList.get(1);
        }

        resultMap.put("assetsDispositionInfo", assetsDispositionInfo);
        resultMap.put("contractNo", contractNo);
        resultMap.put("inquiryDateTimeStr", outputSdf.get().format(inquiryDateTimeObj).toString());
        resultMap.put("generationDateTimeStr", outputSdf.get().format(generationDateTimeObj).toString());
        resultMap.put("inquiryDateTime", inquiryDateTimeObj);
        resultMap.put("generationDateTime", generationDateTimeObj);
        resultMap.put("clientName", clientName);
        resultMap.put("cardType", cardType);
        resultMap.put("cardNo", cardNo);
        resultMap.put("isMarried", isMarried);

    }

    @Override
    public void getCreditInfo(String text, Map<String, Object> resultMap) {

        var totalAccountMap = new LinkedHashMap<String, String>();
        var activeAccountMap = new LinkedHashMap<String, String>();
        var overdueAccountMap = new LinkedHashMap<String, String>();
        var overdue90DaysAccountMap = new LinkedHashMap<String, String>();
        var warrantAccountMap = new LinkedHashMap<String, String>();
        resultMap.put("totalAccount", totalAccountMap);
        resultMap.put("activeAccount", activeAccountMap);
        resultMap.put("overdueAccount", overdueAccountMap);
        resultMap.put("overdue90DaysAccount", overdue90DaysAccountMap);
        resultMap.put("warrantAccount", warrantAccountMap);

        var totalAccountLine = StringUtils.substringBetween(text, "账户数 ", System.lineSeparator());
        var activeAccountLine = StringUtils.substringBetween(text, "未结清/未销户账户数 ", System.lineSeparator());
        var overdueAccountLine = StringUtils.substringBetween(text, "发生过逾期的账户数 ", System.lineSeparator());
        var overdue90DaysAccountLine = StringUtils.substringBetween(text, "发生过90天以上逾期的账户数 ", System.lineSeparator());
        var warrantAccountLine = StringUtils.substringBetween(text, "为他人担保笔数 ", System.lineSeparator());

        var totalAccountArray = StringUtils.split(totalAccountLine);
        if (totalAccountArray != null && totalAccountArray.length >= 3) {
            totalAccountMap.put("creditCard", totalAccountArray[0]);
            totalAccountMap.put("houseLoan", totalAccountArray[1]);
            totalAccountMap.put("otherLoan", totalAccountArray[2]);
        }

        var activeAccountArray = StringUtils.split(activeAccountLine);
        if (activeAccountArray != null && activeAccountArray.length >= 3) {
            activeAccountMap.put("creditCard", activeAccountArray[0]);
            activeAccountMap.put("houseLoan", activeAccountArray[1]);
            activeAccountMap.put("otherLoan", activeAccountArray[2]);
        }

        var overdueAccountArray = StringUtils.split(overdueAccountLine);
        if (overdueAccountArray != null && overdueAccountArray.length >= 3) {
            overdueAccountMap.put("creditCard", overdueAccountArray[0]);
            overdueAccountMap.put("houseLoan", overdueAccountArray[1]);
            overdueAccountMap.put("otherLoan", overdueAccountArray[2]);
        }


        var overdue90DaysAccountArray = StringUtils.split(overdue90DaysAccountLine);
        if (overdue90DaysAccountArray != null && overdue90DaysAccountArray.length >= 3) {
            overdue90DaysAccountMap.put("creditCard", overdue90DaysAccountArray[0]);
            overdue90DaysAccountMap.put("houseLoan", overdue90DaysAccountArray[1]);
            overdue90DaysAccountMap.put("otherLoan", overdue90DaysAccountArray[2]);
        }


        var warrantAccountArray = StringUtils.split(warrantAccountLine);
        if (warrantAccountArray != null && warrantAccountArray.length >= 3) {
            warrantAccountMap.put("creditCard", warrantAccountArray[0]);
            warrantAccountMap.put("houseLoan", warrantAccountArray[1]);
            warrantAccountMap.put("otherLoan", warrantAccountArray[2]);
        }

    }

    @Override
    public void getInquiryInfo(String text, Map<String, Object> resultMap) {

        var authInquiryDetails = new ArrayList<Map>();
        var personInquiryDetails = new ArrayList<Map>();
        var forceInquiryDetails = new ArrayList<Map>();

        resultMap.put("authInquiryDetails", authInquiryDetails);
        resultMap.put("personInquiryDetails", personInquiryDetails);
        resultMap.put("forceInquiryDetails", forceInquiryDetails);


        var startPos = StringUtils.lastIndexOf(text, "这部分包含您的信用报告最近2年内被查询的记录");
        var inquiryLines = StringUtils.substring(text, startPos);
        var forcePos = StringUtils.indexOf(text, "执行法院");

        var authPos = StringUtils.indexOf(inquiryLines, "机构查询记录明细");
        var personPos = StringUtils.indexOf(inquiryLines, "本人查询记录明细");

        if (authPos != -1 && personPos != -1) {
            dealAuthInquiryDetails(inquiryLines, authInquiryDetails);
            dealPersonInquiryDetails(inquiryLines, personInquiryDetails);

        } else if (authPos != -1) {
            dealAuthInquiryDetails(inquiryLines, authInquiryDetails);

        } else if (personPos != -1) {
            dealPersonInquiryDetails(inquiryLines, personInquiryDetails);

        }

        if (forcePos != -1) {
            dealForceInquiryDetails(text, forceInquiryDetails);
        }

    }

    @Override
    public void getOverdueInfo(String text, Map<String, Object> resultMap) {

        var compensatoryDetails = new ArrayList<Map>();
        var creditCardNormalDetails = new ArrayList<Map>();
        var creditCardOverdueDetails = new ArrayList<Map>();
        var houseLoanNormalDetails = new ArrayList<Map>();
        var houseLoanOverdueDetails = new ArrayList<Map>();
        var otherLoanNormalDetails = new ArrayList<Map>();
        var otherLoanOverdueDetails = new ArrayList<Map>();

        resultMap.put("compensatoryDetails", compensatoryDetails);
        resultMap.put("creditCardOverdueDetails", creditCardOverdueDetails);
        resultMap.put("creditCardNormalDetails", creditCardNormalDetails);
        resultMap.put("houseLoanOverdueDetails", houseLoanOverdueDetails);
        resultMap.put("houseLoanNormalDetails", houseLoanNormalDetails);
        resultMap.put("otherLoanOverdueDetails", otherLoanOverdueDetails);
        resultMap.put("otherLoanNormalDetails", otherLoanNormalDetails);

        var compensatoryLines = StringUtils.substringBetween(text, "保证人代偿信息", "账户明细");
        var compensatoryArray = StringUtils.split(compensatoryLines, System.lineSeparator());
        if (compensatoryArray != null && compensatoryArray.length > 0) {
            var compensatoryList = Arrays.asList(compensatoryArray);
            for (var i = 0; i < compensatoryList.size(); i++){
                var l = compensatoryList.get(i);
                if (l.matches(regexForDate)) {
                    if(!l.contains("余额")){
                        l = StringUtils.join(l, compensatoryList.get(i + 1));
                    }
                    var map = new LinkedHashMap<String, String>();
                    var compensatoryDate = StringUtils.substringBefore(l, "日");
                    compensatoryDate = compensatoryDate.replace("年", "-")
                            .replace("月", "-")
                            .replace("日", "");
                    map.put("compensatoryDate", compensatoryDate);

                    var compensatoryAuthority = StringUtils.substringBetween(l, "日", "进行最近一次代偿");
                    map.put("compensatoryAuthority", compensatoryAuthority);

                    var compensatoryTotalAmount = StringUtils.substringBetween(l, "代偿金额", "。");
                    compensatoryTotalAmount = StringUtils.removeAll(compensatoryTotalAmount, "[,，。]");
                    map.put("compensatoryTotalAmount", compensatoryTotalAmount);

                    var compensatoryRemainAmount = StringUtils.substringAfter(l, "余额");
                    compensatoryRemainAmount = StringUtils.removeAll(compensatoryRemainAmount, "[,，。]");
                    compensatoryRemainAmount = StringUtils.trim(compensatoryRemainAmount);
                    map.put("compensatoryRemainAmount", compensatoryRemainAmount);
                    var compensatoryLatestRepaymentDate = "";
                    if(l.contains("一次还款日期为")){
                        compensatoryLatestRepaymentDate = StringUtils.substringBetween(l, "一次还款日期为", "，余额").replace("年", "-").replace("月", "-").replace("日", "");
                    }
                    map.put("compensatoryLatestRepaymentDate", compensatoryLatestRepaymentDate);

                    compensatoryDetails.add(map);
                }
            }
        }


        var overdueLines = StringUtils.substringBetween(text, "个人信用报告", "公共记录");
        overdueLines = "信用卡" + System.lineSeparator() + overdueLines;
        var lines = StringUtils.split(overdueLines, System.lineSeparator());
        var lineList = Arrays.asList(lines);
        lineList = lineList.stream()
                .filter(s -> s.contains("发生过逾期的贷记卡账户明细如下")
                        || s.contains("透支超过60天的准贷记卡账户明细如下")
                        || s.contains("从未逾期过的贷记卡及透支未超过60天的准贷记卡账户明细如下")
                        || s.contains("发生过逾期的账户明细如下")
                        || s.contains("从未逾期过的账户明细如下")
                        || s.matches("^信用卡\\s?$")
                        || s.matches("^购房贷款\\s?$")
                        || s.matches("^其他贷款\\s?$")
                        || s.contains("。")
                        || s.contains("，")
                        || s.matches(regexForDate))
                .collect(Collectors.toList());

        var refinedLineList = new ArrayList<String>();
        int creditCardPos = -1;
        int houseLoanPos = -1;
        int otherLoanPos = -1;

        for (var l : lineList) {
            if ((l.matches(regexForDate)
                    || l.contains("发生过逾期的贷记卡账户明细如下")
                    || l.contains("透支超过60天的准贷记卡账户明细如下")
                    || l.contains("从未逾期过的贷记卡及透支未超过60天的准贷记卡账户明细如下")
                    || l.contains("发生过逾期的账户明细如下")
                    || l.contains("从未逾期过的账户明细如下"))
                    && (l.contains("发放的"))) {

                refinedLineList.add(l);
            } else if (l.matches("^信用卡\\s?$")) {
                refinedLineList.add(l);
                creditCardPos = refinedLineList.size() - 1;
            } else if (l.matches("^购房贷款\\s?$")) {
                refinedLineList.add(l);
                houseLoanPos = refinedLineList.size() - 1;
            } else if (l.matches("^其他贷款\\s?$")) {
                refinedLineList.add(l);
                otherLoanPos = refinedLineList.size() - 1;
            } else {
                var lastPos = refinedLineList.size() - 1;
                refinedLineList.set(lastPos, refinedLineList.get(lastPos) + l);
            }

        }

        if (creditCardPos != -1) {
            int endPos;
            if (houseLoanPos != -1) {
                endPos = houseLoanPos - 1;
            } else if (otherLoanPos != -1) {
                endPos = otherLoanPos - 1;
            } else {
                endPos = refinedLineList.size() - 1;
            }
            var startCheckContent = refinedLineList.contains("信用卡发生过逾期的贷记卡账户明细如下： ") ?
                    "发生过逾期的贷记卡账户明细如下" : refinedLineList.contains("信用卡透支超过60天的准贷记卡账户明细如下： ") ?
                    "透支超过60天的准贷记卡账户明细如下" : "";
            splitOverdueAndNorms(creditCardPos,
                    endPos,
                    refinedLineList,
                    creditCardOverdueDetails,
                    creditCardNormalDetails,
                    startCheckContent,
                    "从未逾期过的贷记卡及透支未超过60天的准贷记卡账户明细如下");

        }

        if (houseLoanPos != -1) {
            int endPos;
            if (otherLoanPos != -1) {
                endPos = otherLoanPos - 1;
            } else {
                endPos = refinedLineList.size() - 1;
            }

            splitOverdueAndNorms(houseLoanPos,
                    endPos,
                    refinedLineList,
                    houseLoanOverdueDetails,
                    houseLoanNormalDetails,
                    "发生过逾期的账户明细如下",
                    "从未逾期过的账户明细如下");

        }

        if (otherLoanPos != -1) {
            var endPos = refinedLineList.size() - 1;

            splitOverdueAndNorms(otherLoanPos,
                    endPos,
                    refinedLineList,
                    otherLoanOverdueDetails,
                    otherLoanNormalDetails,
                    "发生过逾期的账户明细如下",
                    "从未逾期过的账户明细如下");

        }

    }

    @Override
    public void getWarrantInfo(String text, Map<String, Object> resultMap) {

        var warrantDetails = new ArrayList<Map>();
        var warrantCreditDetails = new ArrayList<Map>();
        resultMap.put("warrantDetails", warrantDetails);
        resultMap.put("warrantCreditDetails",warrantCreditDetails);
        var warrantPos = StringUtils.indexOf(text, "为他人担保信");
        if (warrantPos != -1) {
            var warrantLines = StringUtils.substringBetween(text, "为他人担保信息", "公共记录");
            var warrantArray = StringUtils.split(warrantLines, System.lineSeparator());
            var warrantList = Arrays.asList(warrantArray);
            var warrantLineList = new ArrayList<String>();

            for (var l : warrantList) {
                if (l.matches(regexForDate) && l.contains("身份证")) {
                    if (!l.contains("担保贷款合同金额") || !l.contains("担保信用卡")) {
                        var nextPos = warrantList.indexOf(l) + 1;
                        warrantLineList.add(l + warrantList.get(nextPos));
                    }
                }
            }

            for (var w : warrantLineList) {
                var map = new LinkedHashMap<String, String>();
                var dateArr = StringUtils.split(StringUtils.substringBefore(w, "，为"), "年");
                map.put("warrantStartDate", (StringUtils.substring(dateArr[0], dateArr[0].length() - 4, dateArr[0].length())
                        + "年" + dateArr[1]).replace("年", "-").replace("月", "-").replace("日", ""));
                map.put("warrantName", StringUtils.substringBetween(w, "，为", "（"));
                map.put("warrantCardType", StringUtils.substringBetween(w, "证件类型：", "，证件号码"));
                map.put("warrantCardNumber", StringUtils.substringBetween(w, "证件号码：", "）在"));
                if(w.contains("担保信用卡")){
                    map.put("warrantCreditAddress", StringUtils.substringBetween(w, "）在", "，担保信用卡授信额度"));
                    map.put("warrantCreditLineMoney", StringUtils.substringBetween(w, "担保信用卡授信额度", "，担保金额"));
                    map.put("warrantCreditMoney", StringUtils.substringBetween(w, "担保金额", "。截至"));
                    map.put("warrantCreditUtilDate", StringUtils.substringBetween(w, "截至", "，担保信用卡已用额度").replace("年", "-").replace("月", "-").replace("日", ""));
                    map.put("warrantCreditUsedAmount", StringUtils.substringBetween(w, "，担保信用卡已用额度", "。"));
                    warrantCreditDetails.add(map);
                }else{
                    map.put("warrantAddress", StringUtils.substringBetween(w, "）在", "，担保贷款合同金额"));
                    map.put("warrantLoanMoney", StringUtils.substringBetween(w, "担保贷款合同金额", "，担保金额"));
                    map.put("warrantMoney", StringUtils.substringBetween(w, "担保金额", "。截至"));
                    map.put("warrantUtilDate", StringUtils.substringBetween(w, "截至", "，担保贷款余额").replace("年", "-").replace("月", "-").replace("日", ""));
                    map.put("warrantRemainAmount", StringUtils.substringBetween(w, "，担保贷款余额", "。"));
                    warrantDetails.add(map);
                }
            }
        }

    }

    /**
     * 截取强制执行记录
     *
     * @param text
     * @param list
     */
    private void dealForceInquiryDetails(String text, List<Map> list) {
        var forcePos = StringUtils.indexOf(text, "执行法院");
        var forcePosAllLines = StringUtils.substring(text, forcePos);
        var forcePosLines = "执行法院" + StringUtils.substringBetween(forcePosAllLines, "执行法院", "查询记录");
        var forceLineArray = StringUtils.split(forcePosLines, System.lineSeparator());
        var forceRawList = Arrays.asList(forceLineArray);
        var map = new LinkedHashMap<String, String>();
        for (var str : forceRawList) {
            if (StringUtils.contains(str, "执行法院：") && StringUtils.contains(str, "案号：")) {
                map.put("court", StringUtils.substringBetween(str, "执行法院：", " 案号"));
                map.put("order", StringUtils.substringAfter(str, "案号："));
            } else if (StringUtils.contains(str, "执行案由：") && StringUtils.contains(str, "结案方式:")) {
                map.put("executeReason", StringUtils.substringBetween(str, "执行案由：", " 结案方式"));
                map.put("endType", StringUtils.substringAfter(str, "结案方式:"));
            } else if (StringUtils.contains(str, "立案时间：") && StringUtils.contains(str, "案件状态：")) {
                map.put("caseStart", StringUtils.substringBetween(str, "立案时间：", " 案件状态").replace("年", "-").replace("月", ""));
                map.put("caseStatus", StringUtils.substringAfter(str, "案件状态："));
            } else if (StringUtils.contains(str, "申请执行标的：") && StringUtils.contains(str, "已执行标的：")) {
                map.put("applyExecute", StringUtils.substringBetween(str, "申请执行标的：", " 已执行标的").replace("--", ""));
                map.put("hasExecuted", StringUtils.substringAfter(str, "已执行标的：").replace("--", ""));
            } else if (StringUtils.contains(str, "申请执行标的金额：") && StringUtils.contains(str, "已执行标的金额：")) {
                map.put("applyMoney", StringUtils.substringBetween(str, "申请执行标的金额：", " 已执行标的金额"));
                map.put("hasMoney", StringUtils.substringAfter(str, "已执行标的金额：").replace("--", ""));
            } else if (StringUtils.contains(str, "结案时间：")) {
                map.put("endTime", StringUtils.substringAfter(str, "结案时间：").replace("年", "-").replace("月", ""));
            }
        }
        list.add(map);
    }


    private void dealAuthInquiryDetails(String inquiryLines, List<Map> list) {

        var authPos = StringUtils.indexOf(inquiryLines, "机构查询记录明细");
        var personPos = StringUtils.indexOf(inquiryLines, "本人查询记录明细");

        var authLines = StringUtils.substring(inquiryLines, authPos, personPos);
        var authMoreRow = "";
        if (StringUtils.contains(authLines, System.lineSeparator()+"贷后管理")
                || StringUtils.contains(authLines, System.lineSeparator()+"贷款审批")
                || StringUtils.contains(authLines, System.lineSeparator()+"保前审查")
                || StringUtils.contains(authLines, System.lineSeparator()+"信用卡审批")
                || StringUtils.contains(authLines, System.lineSeparator()+"担保资格审查")
                || StringUtils.contains(authLines, System.lineSeparator()+"资信审查")
                || StringUtils.contains(authLines, System.lineSeparator()+"特约商户实名审查")
                || StringUtils.contains(authLines, System.lineSeparator()+"保后管理")
                || StringUtils.contains(authLines, System.lineSeparator()+"客户准入资格审查")
                || StringUtils.contains(authLines, System.lineSeparator()+"融资审批")
                || StringUtils.contains(authLines, System.lineSeparator()+"异议核查")
                || StringUtils.contains(authLines, System.lineSeparator()+"公积金提取复核")
                || StringUtils.contains(authLines, System.lineSeparator()+"法人代表、负责人、")
                || StringUtils.contains(authLines, System.lineSeparator()+"高管等资信审查")
                ) {
            authMoreRow = "auth";
        }
        var authLineArray = StringUtils.split(authLines, System.lineSeparator());
        var authLineRawList = Arrays.asList(authLineArray);
        setInquiryDetails(authLineRawList, list, authMoreRow);

    }

    private void dealPersonInquiryDetails(String inquiryLines, List<Map> list) {

        var personPos = StringUtils.indexOf(inquiryLines, "本人查询记录明细");

        var personLines = StringUtils.substring(inquiryLines, personPos);
        personLines = StringUtils.replace(personLines, String.format("互联网个%s人信用信息服务平台%s",
                System.lineSeparator(), System.lineSeparator()),
                "互联网个人信用信息服务平台");
        var personMoreRow = "";
        if (StringUtils.contains(personLines, System.lineSeparator()+"本人查询")) {
            personMoreRow = "person";
        }
        var personLineArray = StringUtils.split(personLines, System.lineSeparator());
        var personLineRawList = Arrays.asList(personLineArray);
        setInquiryDetails(personLineRawList, list, personMoreRow);

    }

    private void setInquiryDetails(List<String> input, List<Map> output, String moreRow) {
        if (StringUtils.isNotBlank(moreRow)) {
            //个人查询记录一行信息多行显示
            if (StringUtils.equals(moreRow, "person")) {
                for (var i = 0; i < input.size(); i++) {
                    var s = input.get(i);
                    if (s.contains("年") && s.contains("月") && s.contains("日") && (!s.contains("本人查询"))) {
                        //多行信息拼接到一行
                        input.set(i, StringUtils.join(s, input.get(i + 1), " ", input.get(i + 2)));
                    }
                }
            } else if (StringUtils.equals(moreRow, "auth")) { //机构查询记录一行信息多行显示
                for (var i = 0; i < input.size(); i++) {
                    var s = input.get(i);
                    if (s.contains("年") && s.contains("月") && s.contains("日")
                            && (!s.contains("贷后管理")
                            && !s.contains("贷款审批")
                            && !s.contains("保前审查")
                            && !s.contains("信用卡审批")
                            && !s.contains("担保资格审查")
                            && !s.contains("资信审查")
                            && !s.contains("特约商户实名审查")
                            && !s.contains("保后管理")
                            && !s.contains("资格审查")
                            && !s.contains("客户准入资格审查")
                            && !s.contains("融资审批")
                            && !s.contains("异议核查")
                            && !s.contains("公积金提取复核")
                            && !s.contains("法人代表、负责人、")
                    )) {
                        //多行信息拼接到一行
                        input.set(i, StringUtils.join(s, input.get(i + 1), " ", input.get(i + 2)));
                    }else if(s.contains("年") && s.contains("月") && s.contains("日") && s.contains("法人代表、负责人、")){
                        input.set(i, StringUtils.join(s, input.get(i + 1)));
                    }
                }
            }
        }
        var personLineCleanList = input.stream()
                .filter(l -> l.contains("年") && l.contains("月") && l.contains("日")).collect(Collectors.toList());

        for (var s : personLineCleanList) {
            var arr = StringUtils.split(s, " ");
            if (arr.length >= 4) {
                var map = new LinkedHashMap<String, String>();
                map.put("date", arr[1]
                        .replace("年", "-")
                        .replace("月", "-")
                        .replace("日", ""));
                map.put("operator", arr[2]);
                map.put("cause", arr[3]);
                output.add(map);

            }
        }
    }

    private void splitOverdueAndNorms(int startPos,
                                      int endPos,
                                      ArrayList<String> sourceList,
                                      ArrayList<Map> outputOverdueList,
                                      ArrayList<Map> outputNormList,
                                      String startCheckContent,
                                      String endCheckContent) {

        int overduePos = endPos;
        int normPos = endPos;
        /*找到逾期和正常记录的坐标点*/
        for (var i = startPos; i <= endPos; i++) {
            if (sourceList.get(i).contains(startCheckContent)) {
                overduePos = i;
            }
            if (sourceList.get(i).contains(endCheckContent)) {
                normPos = i;
            }
        }

        addOverdueAndNormsValue(overduePos, normPos, sourceList, outputOverdueList);
        addOverdueAndNormsValue(normPos, endPos, sourceList, outputNormList);

    }

    private void addOverdueAndNormsValue(int startPos,
                                         int endPos,
                                         ArrayList<String> sourceList,
                                         ArrayList<Map> outputList) {

        if (startPos != -1) {
            for (var i = startPos + 1; i <= endPos; i++) {
                if (sourceList.get(i).matches(regexForDate)) {

                    var map = new LinkedHashMap<String, String>();

                    /*发放信用卡或者贷款的时间*/
                    var cardDistributionDate = StringUtils.substringBefore(sourceList.get(i), "日");
                    var dateArr = StringUtils.split(cardDistributionDate, "年");
                    cardDistributionDate = (StringUtils.substring(dateArr[0], dateArr[0].length() - 4, dateArr[0].length())
                            + "年" + dateArr[1]).replace("年", "-").replace("月", "-").replace("日", "");
                    var distributionDate = cardDistributionDate
                            .replace("年", "-")
                            .replace("月", "-")
                            .replace("日", "")
                            .replaceAll("\\d\\.\\s", "");
                    map.put("distributionDate", distributionDate);

                    /*发放机构名称*/
                    var distributionAuthority = StringUtils.substringBetween(sourceList.get(i), "日", "发放的");
                    map.put("distributionAuthority", distributionAuthority);

                    /*发放的项目名称*/
                    var distributionItemName = StringUtils.substringBetween(sourceList.get(i), "发放的", "）") + "）";
                    distributionItemName = StringUtils.replaceAll(distributionItemName, "[，,]", "");
                    map.put("distributionItemName", distributionItemName);

                    /*最近一次查询的时间*/
                    var recentUpdateDateStr = "";
                    if(sourceList.get(i).contains("担保")){
                        recentUpdateDateStr = StringUtils.substringBefore(sourceList.get(i),"担保");
                    }else {
                        recentUpdateDateStr = sourceList.get(i);
                    }
                    var recentUpdateDate = StringUtils.substringBetween(recentUpdateDateStr, "截至", "月") + "月";
                    if (StringUtils.isNotBlank(recentUpdateDate)) {
                        recentUpdateDate = recentUpdateDate
                                .replace("年", "-")
                                .replace("月", "")
                                .replace("null", "");
                    } else {
                        recentUpdateDate = "";
                    }
                    map.put("recentUpdateDate", recentUpdateDate);

                    /*近五年来一共存在了几个月的逾期*/
                    var recent5YearsOverdueNumOfMonth = "";
                    var recent5YearsOverdueNumOfMonthstr = sourceList.get(i);
                    if(recent5YearsOverdueNumOfMonthstr.contains("从未逾期过的贷记卡及透支")){
                        recent5YearsOverdueNumOfMonthstr = StringUtils.substringBefore(recent5YearsOverdueNumOfMonthstr,"从未逾期过的贷记卡及透支");
                    }
                    if(recent5YearsOverdueNumOfMonthstr.contains("透支超过60天的准贷记卡账户明细如下")){
                        recent5YearsOverdueNumOfMonthstr = StringUtils.substringBefore(recent5YearsOverdueNumOfMonthstr,"透支超过60天的准贷记卡账户明细如下");
                    }
                    if(recent5YearsOverdueNumOfMonthstr.contains("准贷记卡")){
                        recent5YearsOverdueNumOfMonth = StringUtils.substringBetween(recent5YearsOverdueNumOfMonthstr, "最近5年内有", "个月透支超过60天");
                    }else{
                        recent5YearsOverdueNumOfMonth = StringUtils.substringBetween(recent5YearsOverdueNumOfMonthstr, "最近5年内有", "个月处于逾期状态");
                    }
                    if (StringUtils.isNotBlank(recent5YearsOverdueNumOfMonth)) {
                        recent5YearsOverdueNumOfMonth = recent5YearsOverdueNumOfMonth
                                .replace("年", "-")
                                .replace("月", "");
                    }
                    map.put("recent5YearsOverdueNumOfMonth", recent5YearsOverdueNumOfMonth);

                    /*是否有超过连续90天以上的逾期*/
                    map.put("hasOver90DaysOverdueRecord", null);
                     /*是否有超过连续90天以上的逾期的月数*/
                    map.put("hasOver90DaysOverdueMonths", null);
                    if(StringUtils.isNotBlank(recent5YearsOverdueNumOfMonth)){
                        if (StringUtils.contains(sourceList.get(i), "没有发生过90天以上逾期") || StringUtils.contains(sourceList.get(i), "没有发生过90天以上透支")) {
                            map.put("hasOver90DaysOverdueRecord", Boolean.FALSE.toString());
                        }else{
                            map.put("hasOver90DaysOverdueRecord", Boolean.TRUE.toString());
                            var hasOver90DaysOverdueMonths = sourceList.get(i);
                            if(hasOver90DaysOverdueMonths.contains("从未逾期过的贷记卡及透支")){
                                hasOver90DaysOverdueMonths = StringUtils.substringBefore(hasOver90DaysOverdueMonths,"从未逾期过的贷记卡及透支");
                            }
                            if(hasOver90DaysOverdueMonths.contains("透支超过60天的准贷记卡账户明细如下")){
                                hasOver90DaysOverdueMonths = StringUtils.substringBefore(hasOver90DaysOverdueMonths,"透支超过60天的准贷记卡账户明细如下");
                            }
                            if(hasOver90DaysOverdueMonths.contains("准贷记卡")){
                                map.put("hasOver90DaysOverdueMonths", StringUtils.substringBetween(hasOver90DaysOverdueMonths, "透支超过60天，其中", "个月透支超过90天"));
                            }else{
                                map.put("hasOver90DaysOverdueMonths", StringUtils.substringBetween(hasOver90DaysOverdueMonths, "逾期状态，其中", "个月逾期超过90天"));
                            }
                        }
                    }

                    /*信用卡是否激活*/
                    var creditCardIsActive = Boolean.TRUE.toString();
                    if (StringUtils.contains(sourceList.get(i), "尚未激活")) {
                        creditCardIsActive = Boolean.FALSE.toString();
                    }
                    map.put("creditCardIsActive", creditCardIsActive);

                    /*信用卡信用额度*/
                    var creditCardTotalAmount = "";
                    var creditCardTotalAmountstr = sourceList.get(i);
                    if(creditCardTotalAmountstr.contains("从未逾期过的贷记卡及透支")){
                        creditCardTotalAmountstr = StringUtils.substringBefore(creditCardTotalAmountstr,"从未逾期过的贷记卡及透支");
                    }
                    if(creditCardTotalAmountstr.contains("透支超过60天的准贷记卡账户明细如下")){
                        creditCardTotalAmountstr = StringUtils.substringBefore(creditCardTotalAmountstr,"透支超过60天的准贷记卡账户明细如下");
                    }
                    if(creditCardTotalAmountstr.contains("准贷记卡")){
                        creditCardTotalAmount = StringUtils.substringBetween(creditCardTotalAmountstr, "信用额度", "，透支余额");
                    }else{
                        creditCardTotalAmount = StringUtils.substringBetween(creditCardTotalAmountstr, "信用额度", "已使用");
                    }

                    if (StringUtils.isBlank(creditCardTotalAmount)) {
                        creditCardTotalAmount = StringUtils.substringBetween(sourceList.get(i), "信用额度", "尚未激活");
                    }
                    creditCardTotalAmount = StringUtils.replaceAll(creditCardTotalAmount, "[，。,]", "");
                    map.put("creditCardTotalAmount", creditCardTotalAmount);

                    /*信用卡已使用额度*/
                    var creditCardUsedAmount = StringUtils.substringBetween(sourceList.get(i), "已使用额度", "。");
                    if (StringUtils.contains(creditCardUsedAmount, "逾期金额")) {
                        creditCardUsedAmount = StringUtils.substringBetween(sourceList.get(i), "已使用额度", "逾期金额");
                    }
                    creditCardUsedAmount = StringUtils.replaceAll(creditCardUsedAmount, "[，,]", "");
                    map.put("creditCardUsedAmount", creditCardUsedAmount);

                    /*信用卡逾期额度*/
                    var creditCardOverdueAmount = "";
                    var creditCardOverdueAmountstr = sourceList.get(i);
                    if(creditCardOverdueAmountstr.contains("从未逾期过的贷记卡及透支")){
                        creditCardOverdueAmountstr = StringUtils.substringBefore(creditCardOverdueAmountstr,"从未逾期过的贷记卡及透支");
                    }
                    if(creditCardOverdueAmountstr.contains("透支超过60天的准贷记卡账户明细如下")){
                        creditCardOverdueAmountstr = StringUtils.substringBefore(creditCardOverdueAmountstr,"透支超过60天的准贷记卡账户明细如下");
                    }
                    if(creditCardOverdueAmountstr.contains("准贷记卡") ){
                        creditCardUsedAmount =  StringUtils.substringBetween(creditCardOverdueAmountstr, "透支余额", "。");
                    }else{
                        creditCardOverdueAmount = StringUtils.substringBetween(creditCardOverdueAmountstr, "逾期金额", "。");
                    }
                    creditCardUsedAmount = StringUtils.replaceAll(creditCardUsedAmount, "[，,]", "");
                    map.put("creditCardUsedAmount", creditCardUsedAmount);
                    creditCardOverdueAmount = StringUtils.replaceAll(creditCardOverdueAmount, "[，,]", "");
                    map.put("creditCardOverdueAmount", creditCardOverdueAmount);

                    /*信用卡客户注销卡的日期*/
                    var creditCardCancellationDate = StringUtils.substringBetween(sourceList.get(i), "截至", "已销户");
                    if (StringUtils.isNotBlank(creditCardCancellationDate)) {
                        creditCardCancellationDate = creditCardCancellationDate
                                .replace("年", "-")
                                .replace("月", "");
                    }
                    map.put("creditCardCancellationDate", creditCardCancellationDate);

                    /*贷款到期时间*/
                    var loanEndDate = StringUtils.substringBetween(sourceList.get(i), "贷款，", "到期");
                    if (StringUtils.isNotBlank(loanEndDate)) {
                        loanEndDate = loanEndDate
                                .replace("年", "-")
                                .replace("月", "-")
                                .replace("日", "");
                    }
                    map.put("loanEndDate", loanEndDate);

                    /*贷款是否呆账*/
                    var loanIsBadDebit = Boolean.FALSE.toString();
                    if (StringUtils.contains(sourceList.get(i), "呆账")) {
                        loanIsBadDebit = Boolean.TRUE.toString();
                    }
                    map.put("loanIsBadDebit", loanIsBadDebit);

                    /*贷款剩余未还金额*/
                    var loanRemainAmountStr = "";
                    if(sourceList.get(i).contains("担保")){
                        loanRemainAmountStr = StringUtils.substringBefore(sourceList.get(i),"担保");
                    }else {
                        loanRemainAmountStr = sourceList.get(i);
                    }
                    var loanRemainAmount = StringUtils.substringBetween(loanRemainAmountStr, "余额", "。");
                    loanRemainAmount = StringUtils.replaceAll(loanRemainAmount, "[,，]", "");
                    if (StringUtils.contains(loanRemainAmount, "逾期金额")) {
                        var remainArr = loanRemainAmount.split("逾期金额");
                        if (remainArr.length > 0) {
                            loanRemainAmount = remainArr[0];
                        }
                    }
                    map.put("loanRemainAmount", loanRemainAmount);



                    /*剩余还款金额*/
                    var loanBalanceAmount = StringUtils.substringBetween(sourceList.get(i), "余额", "。");
                    loanBalanceAmount = StringUtils.replaceAll(loanBalanceAmount, "[,，]", "");
                   /*当前逾期金额*/
                    var loanOverdueAmount = "";
                    if (StringUtils.contains(loanBalanceAmount, "逾期金额")) {
                        var overdueArr = loanBalanceAmount.split("逾期金额");
                        if (overdueArr.length > 1) {
                            loanOverdueAmount = loanBalanceAmount.split("逾期金额")[1];
                        }
                    }
                    map.put("loanOverdueAmount", loanOverdueAmount);

                    /*贷款客户结清日期*/
                    var loanSettlementDate = StringUtils.substringBetween(sourceList.get(i), "，", "已结清");
                    if (StringUtils.isBlank(loanSettlementDate)) {
                        loanSettlementDate = StringUtils.substringBetween(sourceList.get(i), "，", "已转出");
                    }
                    if (StringUtils.isNotBlank(loanSettlementDate)) {
                        loanSettlementDate = loanSettlementDate
                                .replace("年", "-")
                                .replace("月", "");
                    }
                    map.put("loanSettlementDate", loanSettlementDate);
                    outputList.add(map);
                }
            }
        }

    }

}
