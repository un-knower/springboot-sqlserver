package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.LoanInfo;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.service.service.PersonCreditProcessor;
import com.yingu.project.service.service.simple.xdoc.DocDate;
import com.yingu.project.service.service.simple.xdoc.DocMoney;
import com.yingu.project.service.service.simple.xdoc.XdocUtil;
import com.yingu.project.util.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class LoanInfoProcessor extends PersonCreditProcessor {
    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.LoanInfoProcessor.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        try {
            List<LoanInfo> list = getInfo(personCreditParam.getXdoc());
            if (null != list) {
                personCredit.setLoanInfoList(list);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("LoanInfoProcessor.analyse", e);
            return false;
        }
    }

    public List<LoanInfo> getInfo(XWPFDocument xdoc) {
        List<XWPFTable> tables = DocUtil.getTablesItem(KeywordBegin, KeywordEnd, xdoc);
        //识别分页截断的table，拼接tables

        //确认版本
        int version = new ConfirmVersionUtil(xdoc).confirm();
        if (version == 0) {//明细版
            return setValue_detail(tables.get(0));
        } else if (version == 1) {//2011
            return setValue_2011(xdoc);
        } else {//2012
            return setValue_2012(tables);
        }
    }

    private List<LoanInfo> setValue_detail(XWPFTable table) {
        List<XWPFTableCell> cells;
        //table 2 List<List<XWPFTableRow>>
        List<List<XWPFTableRow>> rowListList = new ArrayList<>();
        List<XWPFTableRow> tableRows = table.getRows();
        int tableRowsSize = tableRows.size();
        for (int i = 0; i < tableRowsSize; i++) {
            XWPFTableRow row = tableRows.get(i);
            cells = row.getTableCells();
            if (cells.size() > 0 && TableUtil.isSimilarCountEqual(cells.get(0), ZhangHuZhuangTai)
                    && i > 0 && i + 3 < tableRowsSize) {
                List<XWPFTableRow> rowList = new ArrayList<>();
                for (int j = i - 1; j <= i + 3; j++) {
                    rowList.add(tableRows.get(j));
                }
                rowListList.add(rowList);
            }
        }

        List<LoanInfo> list = new ArrayList<>();
        for (int i = 0; i < rowListList.size(); i++) {
            List<XWPFTableRow> rowList = rowListList.get(i);
            LoanInfo entity = new LoanInfo();
            try {
                cells = rowList.get(0).getTableCells();
                String outline = TableUtil.text(cells.get(0));//概要
                outline(entity, outline);

                cells = rowList.get(2).getTableCells();
                entity.setAccountStatus(TableUtil.text(cells.get(0)));
                entity.setFiveCategories(TableUtil.text(cells.get(1)));
                entity.setBalance(TableUtil.text(cells.get(2)));
                entity.setRemainingRepaymentPeriods(TableUtil.text(cells.get(3)));
                entity.setRepayableAmountTheMonth(TableUtil.text(cells.get(4)));
                entity.setRepayableDate(TableUtil.text(cells.get(5)));
                entity.setPaidInAmountTheMonth(TableUtil.text(cells.get(6)));
                entity.setLatestRepaymentDate(TableUtil.text(cells.get(7)));

                cells = rowList.get(4).getTableCells();
                entity.setCurrentOverduePeriods(TableUtil.text(cells.get(0)));
                entity.setCurrentOverdueAmount(TableUtil.text(cells.get(1)));
                entity.setPrincipleAmount31_60DaysPastDue(TableUtil.text(cells.get(2)));
                entity.setPrincipleAmount61_90DaysPastDue(TableUtil.text(cells.get(3)));
                entity.setPrincipleAmount91_180DaysPastDue(TableUtil.text(cells.get(4)));
                entity.setPrincipleAmount180DaysPlusPastDue(TableUtil.text(cells.get(5)));
                list.add(entity);
            } catch (Exception e) {
                log.error("LoanInfoProcessor.setValue_detail", e);
            }
        }
        return list;
    }

    //概要
    private void outline(LoanInfo entity, String outline) {
        //如果前3位有.，就把.及其之前的去掉
        String serialNumber = outline.substring(0, 3);
        int iii = serialNumber.indexOf(".");
        int ii2 = serialNumber.indexOf("、");
        int ii3 = serialNumber.indexOf("*");
        iii = iii > ii2 ? iii : ii2;
        iii = iii > ii3 ? iii : ii3;
        if (iii > -1) {
            outline = outline.substring(iii + 1, outline.length());
        }
        //替换错别字
        outline = TableUtil.typoOutline(outline);
        //将,(英文逗号)替换为，(中文逗号)
        outline = TableUtil.typoCommaSymbol_outline(outline);

        //发放日期
        if (outline.length() > 11) {
            String cardDistributionDateStrTemp = outline.substring(0, 11);
            DocDate docDate = XdocUtil.analysisPrefixDate(cardDistributionDateStrTemp);
            entity.setIssueDate(docDate.getDateStr());
        }
        //贷款机构，固定"机构"开头，“发放”结尾
        String distributionAuthority = StringUtils.substringBetween(outline, "机构", "发放");
        entity.setLendingInstitution(distributionAuthority);
        //合同金额。贷款种类细分
        String distributionItemName = StringUtils.substringBetween(outline, "发放的", "）");
        if (!StringUtil.isEmpty(distributionItemName) && distributionItemName.length() < 13) {
            if (StringUtils.isNumeric(distributionItemName.substring(0, 1))) {
                //若为数字，则itemStr是贷款类型，此区间字符串是额度
                DocMoney money = XdocUtil.analysisPrefixMoney(distributionItemName);
                entity.setContractAmount(money.getMoneyStr());

                String creditTypeTemp = StringUtils.substringBetween(outline, "）", "，");
                if (!StringUtil.isEmpty(creditTypeTemp)) {
                    String creditType = creditTypeTemp;
                    entity.setLoanType(creditType);
                }
            }
        }
        //业务号
        String sss = StringUtils.substringBetween(outline, "号", "，");
        if (!StringUtil.isEmpty(sss)) {
            entity.setBusinessNo(sss);
        }

        /* 担保方式 */
        int indexBao = outline.indexOf("保");
        if (indexBao != -1) {
            int commaBefore = outline.substring(0, indexBao).lastIndexOf("，");
            int commaAfter = outline.indexOf("，", indexBao + 1);
            sss = outline.substring(commaBefore + 1, commaAfter);
            if (!StringUtil.isEmpty(sss)) {
                entity.setGuarantyStyle(sss);
            }
        }
        /* 还款期数 */
        sss = StringUtils.substringBefore(outline, "期");
        sss = StringUtils.substringAfterLast(sss, "，");
        if (!StringUtil.isEmpty(sss) && StringUtils.isNumeric(sss)) {
            entity.setRepaymentPeriods(sss);
            /* 还款频率 */
            sss = StringUtils.substringBetween(StringUtils.substringAfter(outline, "期"), "，", "，");
            if (!StringUtil.isEmpty(sss)) {
                entity.setRepaymentFrequency(sss);
            }
        } else {//不定期归还
            int indexQi = outline.indexOf("期");
            int commaBefore = outline.substring(0, indexQi).lastIndexOf("，");
            int commaAfter = outline.indexOf("，", indexQi + 1);
            sss = outline.substring(commaBefore + 1, commaAfter);
            if (!StringUtil.isEmpty(sss)) {
                entity.setRepaymentPeriods(sss);
            }
        }
        //到期日期
        sss = StringUtils.substringBefore(outline, "到期");
        sss = StringUtils.substringAfterLast(sss, "，");
        if (!StringUtil.isEmpty(sss)) {
            entity.setDueDate(sss);
        }
        /* 结清日期 */
        sss = StringUtils.substringBetween(outline, "已于", "结清");
        if (!StringUtil.isEmpty(sss)) {
            entity.setJieQingDate(sss);
        }
    }

    private List<LoanInfo> setValue_2011(XWPFDocument xdoc) {
        DocIndex index = DocUtil.getIndex(xdoc, KeywordBegin, KeywordEnd);
        if (null == index)
            return null;
        List<IBodyElement> elements = new ArrayList<>();
        List<IBodyElement> elementsAll = xdoc.getBodyElements();
        for (int i = index.getBegin() + 1; i < index.getEnd(); i++) {
            elements.add(elementsAll.get(i));
        }
        //删除空行
        for (int i = elements.size() - 1; i > -1; i--) {
            IBodyElement element = elements.get(i);
            if (element instanceof XWPFParagraph) {
                XWPFParagraph para = (XWPFParagraph) element;
                String paraText = TableUtil.text(para);
                if ("" == paraText) {
                    elements.remove(i);
                }
            }
        }
        //取全部段落
        List<XWPFParagraph> paras = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            IBodyElement element = elements.get(i);
            if (element instanceof XWPFParagraph) {
                XWPFParagraph para = (XWPFParagraph) element;
                paras.add(para);
            }
        }
        //根据para找对应的表格，并使用para和table，得到LoanInfo
        List<LoanInfo> list = new ArrayList<>();
        for (int i = 0; i < paras.size(); i++) {
            XWPFParagraph para = paras.get(i);
            if (TableUtil.text(para).length() < 72) {//一行最多72个字，概要是两行。
                continue;
            }
            XWPFTable table = getTable_2011(para, elements);
            list.add(value_2011(para, table));
        }
        return list;
    }

    //根据para找对应的表格，段落下面的表格
    private XWPFTable getTable_2011(XWPFParagraph para, List<IBodyElement> elements) {
        int index = elements.indexOf(para);
        if (index + 1 < elements.size()) {
            IBodyElement element = elements.get(index + 1);
            if (element instanceof XWPFTable) {
                return (XWPFTable) element;
            }
        }
        return null;
    }

    //使用para和table赋值
    private LoanInfo value_2011(XWPFParagraph para, XWPFTable table) {
        LoanInfo entity = new LoanInfo();
        //outline para
        String outline = TableUtil.text(para);
        outline(entity, outline);

        if (null != table) {
            List<XWPFTableRow> rows = table.getRows();
            List<XWPFTableCell> cells;
            try {
                if (rows.size() > 3) {
                    cells = rows.get(1).getTableCells();
                    entity.setFiveCategories(TableUtil.text(cells.get(0)));
                    entity.setBalance(TableUtil.text(cells.get(1)));
                    entity.setRemainingRepaymentPeriods(TableUtil.text(cells.get(2)));
                    entity.setRepayableAmountTheMonth(TableUtil.text(cells.get(3)));
                    entity.setRepayableDate(TableUtil.text(cells.get(4)));
                    entity.setPaidInAmountTheMonth(TableUtil.text(cells.get(5)));
                    entity.setLatestRepaymentDate(TableUtil.text(cells.get(6)));
                    cells = rows.get(3).getTableCells();
                    entity.setCurrentOverduePeriods(TableUtil.text(cells.get(0)));
                    entity.setCurrentOverdueAmount(TableUtil.text(cells.get(1)));
                    entity.setPrincipleAmount31_60DaysPastDue(TableUtil.text(cells.get(2)));
                    entity.setPrincipleAmount61_90DaysPastDue(TableUtil.text(cells.get(3)));
                    entity.setPrincipleAmount91_180DaysPastDue(TableUtil.text(cells.get(4)));
                    entity.setPrincipleAmount180DaysPlusPastDue(TableUtil.text(cells.get(5)));
                } else {//结清

                }
            } catch (Exception e) {
                log.error("LoanInfoProcessor.setValue_2011", e);
            }
        }
        return entity;
    }

    private List<LoanInfo> setValue_2012(List<XWPFTable> tables) {
        List<LoanInfo> list = new ArrayList<>();
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        for (int i = 0; i < tables.size(); i++) {
            rows = tables.get(i).getRows();
            LoanInfo entity = new LoanInfo();
            try {
                if (rows.size() < 2)
                    break;
                cells = rows.get(1).getTableCells();
                entity.setLendingInstitution(TableUtil.text(cells.get(0)));
                entity.setBusinessNo(TableUtil.text(cells.get(1)));
                entity.setLoanType(TableUtil.text(cells.get(2)));
                entity.setCurrency(TableUtil.text(cells.get(3)));
                entity.setIssueDate(TableUtil.text(cells.get(4)));
                entity.setDueDate(TableUtil.text(cells.get(5)));
                entity.setContractAmount(TableUtil.text(cells.get(6)));
                entity.setGuarantyStyle(TableUtil.text(cells.get(7)));
                entity.setRepaymentFrequency(TableUtil.text(cells.get(8)));
                entity.setRepaymentPeriods(TableUtil.text(cells.get(9)));
                if (rows.size() < 3)
                    break;
                cells = rows.get(2).getTableCells();
                entity.setAccountStatus(TableUtil.text(cells.get(1)));
                entity.setStateDeadlineDay(TableUtil.text(cells.get(3)));
                if (rows.size() < 5)
                    break;
                cells = rows.get(4).getTableCells();
                entity.setFiveCategories(TableUtil.text(cells.get(0)));
                entity.setBalance(TableUtil.text(cells.get(1)));
                entity.setRemainingRepaymentPeriods(TableUtil.text(cells.get(2)));
                entity.setRepayableAmountTheMonth(TableUtil.text(cells.get(3)));
                entity.setRepayableDate(TableUtil.text(cells.get(4)));
                entity.setPaidInAmountTheMonth(TableUtil.text(cells.get(5)));
                entity.setLatestRepaymentDate(TableUtil.text(cells.get(6)));
                if (rows.size() < 7)
                    break;
                cells = rows.get(6).getTableCells();
                entity.setCurrentOverduePeriods(TableUtil.text(cells.get(0)));
                entity.setCurrentOverdueAmount(TableUtil.text(cells.get(1)));
                entity.setPrincipleAmount31_60DaysPastDue(TableUtil.text(cells.get(2)));
                entity.setPrincipleAmount61_90DaysPastDue(TableUtil.text(cells.get(3)));
                entity.setPrincipleAmount91_180DaysPastDue(TableUtil.text(cells.get(4)));
                entity.setPrincipleAmount180DaysPlusPastDue(TableUtil.text(cells.get(5)));
            } catch (Exception e) {
                log.error("LoanInfoProcessor.setValue_2012", e);
            }
            list.add(entity);
        }
        return list;
    }

    public static String substringBetween(String str, List<String> begins, List<String> ends) {
        return null;
    }

    final List<String> KeywordBegin = Arrays.asList("信贷交易信息明细", "三信贷交易信息明细", "贷款", "（一）贷款");
    final List<String> KeywordEnd = Arrays.asList("贷记卡", "（二）贷记卡");
    final String ZhangHuZhuangTai = "账户状态";
}
