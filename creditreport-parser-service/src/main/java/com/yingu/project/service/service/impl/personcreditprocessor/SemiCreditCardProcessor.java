package com.yingu.project.service.service.impl.personcreditprocessor;

import com.alibaba.fastjson.JSON;
import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.SemiCreditCard;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.service.service.PersonCreditProcessor;
import com.yingu.project.service.service.simple.xdoc.DocDate;
import com.yingu.project.service.service.simple.xdoc.OutlineUtil;
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
public class SemiCreditCardProcessor extends PersonCreditProcessor {
    PersonCreditParam personCreditParam;
    PersonCredit personCredit;
    XWPFDocument xdoc;
    List<IBodyElement> elements;
    List<XWPFParagraph> paras;
    List<XWPFTable> tables;
    List<SemiCreditCard> list = new ArrayList<>();
    List<XWPFTableRow> rows;
    List<XWPFTableCell> cells;

    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.SemiCreditCardProcessor.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        try {
            this.personCreditParam = personCreditParam;
            this.personCredit = personCredit;
            this.xdoc = personCreditParam.getXdoc();
            List<SemiCreditCard> list = getInfo();
            if (null != list) {
                personCredit.setSemiCreditCardList(list);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("analyse", e);
            return false;
        }
    }

    public List<SemiCreditCard> getInfo() {
        DocIndex index = DocUtil.getIndex(xdoc, KeywordBegin, KeywordEnd);
        personCredit.setErrorLog(JSON.toJSONString(index));
        if (index.getBegin() == -1 || index.getEnd() == -1)
            return null;
        elements = DocUtil.getElements(xdoc, index);
        if (DocUtil.listEmpty(elements))
            return null;
        paras = DocUtil.getParas(elements);
        tables = DocUtil.getTables(elements);

        //确认版本
        Integer version = new ConfirmVersionUtil(xdoc).confirm();
        personCredit.setVersionReport(version.toString());
        if (version == 0) {//明细版
            return setValue_detail();
        } else if (version == 1) {//2011
            return setValue_2011();
        } else if (version == 2) {//2012
            return setValue_2012();
        } else {
            return setValue_2011();
        }
    }

    //各版本取值
    private List<SemiCreditCard> setValue_detail() {
        //根据para找对应的表格，并使用para和table，得到LoanInfo
        for (int i = 0; i < paras.size(); i++) {
            XWPFParagraph para = paras.get(i);
            if (TableUtil.text(para).length() < 61) {//概要的最小字数是61，小于61，就是分页截断。
                continue;
            }
            XWPFTable table = DocUtil.getTable(para, elements);
            list.add(value_detail(para, table));
        }
        return list;
    }

    private SemiCreditCard value_detail(XWPFParagraph para, XWPFTable table) {
        SemiCreditCard entity = new SemiCreditCard();
        outline(entity, TableUtil.text(para));
        if (null != table) {
            rows = table.getRows();
            try {
                if (rows.size() > 1) {
                    cells = rows.get(1).getTableCells();
                    entity.setAccountStatus(TableUtil.text(cells.get(0)));//共享额度
                    entity.setOverdraftBalance(TableUtil.text(cells.get(1)));//透支余额
                    entity.setAverageOverdraftBalanceInRecent6Months(TableUtil.text(cells.get(2)));//最近6个月平均透支余额
                    entity.setMaximumOverdraftBalance(TableUtil.text(cells.get(3)));//最大透支余额

                    entity.setStatementDate(TableUtil.text(cells.get(4)));//账单日
                    entity.setPaidInAmountOfTheMonth(TableUtil.text(cells.get(5)));//本月实还款
                    entity.setLatestRepaymentDate(TableUtil.text(cells.get(6)));//最近一次还款日期
                    entity.setOverdraftBalance180DaysPlusUnpaid(TableUtil.text(cells.get(7)));//透支180天以上未付余额
                }
            } catch (Exception e) {
                log.error("value_detail", e);
            }
        }
        return entity;
    }//使用para和table赋值

    private List<SemiCreditCard> setValue_2011() {
        for (int i = 0; i < paras.size(); i++) {
            XWPFParagraph para = paras.get(i);
            //TODO 判断分页截断
            if (TableUtil.text(para).length() < 61) {//概要的最小字数是61，小于61，就是分页截断。
                continue;
            }
            XWPFTable table = DocUtil.getTable(para, elements);
            list.add(value_2011(para, table));
        }
        return list;
    }

    private SemiCreditCard value_2011(XWPFParagraph para, XWPFTable table) {
        SemiCreditCard entity = new SemiCreditCard();
        outline(entity, TableUtil.text(para));

        if (null != table) {
            rows = table.getRows();
            try {
                if (rows.size() > 1) {
                    cells = rows.get(1).getTableCells();
                    entity.setSharedCredit(TableUtil.text(cells.get(0)));//共享额度
                    entity.setOverdraftBalance(TableUtil.text(cells.get(1)));//透支余额
                    entity.setAverageOverdraftBalanceInRecent6Months(TableUtil.text(cells.get(2)));//最近6个月平均透支余额
                    entity.setMaximumOverdraftBalance(TableUtil.text(cells.get(3)));//最大透支余额

                    entity.setStatementDate(TableUtil.text(cells.get(4)));//账单日
                    entity.setPaidInAmountOfTheMonth(TableUtil.text(cells.get(5)));//本月实还款
                    entity.setLatestRepaymentDate(TableUtil.text(cells.get(6)));//最近一次还款日期
                    entity.setOverdraftBalance180DaysPlusUnpaid(TableUtil.text(cells.get(7)));//透支180天以上未付余额
                }
            } catch (Exception e) {
                log.error("value_2011", e);
            }
        }
        return entity;
    }

    private List<SemiCreditCard> setValue_2012() {
        personCredit.setErrorLog("SemiCreditCardProcessor.setValue_2012 NotImplemented. ");
        log.info("setValue_2012 NotImplemented.\n" + personCreditParam.getWordName());
        return null;
    }

    //概要
    private void outline(SemiCreditCard entity, String outline) {
        outline = TableUtil.outlineFormat(outline);

        //发卡日期
        entity.setIssueDate(OutlineUtil.IssueDate(outline));
        //发卡机构
        entity.setCardIssuer(OutlineUtil.CardIssuer(outline));
        //币种
        entity.setCurrency(OutlineUtil.Currency(outline));
        //业务号
        entity.setBusinessNo(OutlineUtil.BusinessNo(outline));

        //额度
        entity.setLineOfCredit(OutlineUtil.LineOfCredit(outline));
        //担保方式
        entity.setGuarantyStyle(OutlineUtil.GuarantyStyle(outline));
    }

    final List<String> KeywordBegin = Arrays.asList("准贷记卡", "（二）准贷记卡", "（三）准贷记卡");
    final List<String> KeywordEnd = Arrays.asList("公共信息明细", "四公共信息明细"
            , "查询记录", "四查询记录", "五查询记录");
}
