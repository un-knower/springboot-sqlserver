package com.yingu.project.service.service.impl.personcreditprocessor;

import com.alibaba.fastjson.JSON;
import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.CreditCard;
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
public class CreditCardProcessor extends PersonCreditProcessor {
    PersonCreditParam personCreditParam;
    PersonCredit personCredit;
    XWPFDocument xdoc;
    List<IBodyElement> elements;
    List<XWPFParagraph> paras;
    List<XWPFTable> tables;
    List<CreditCard> list = new ArrayList<>();
    List<XWPFTableRow> rows;
    List<XWPFTableCell> cells;

    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.CreditCardProcessor.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        try {
            this.personCreditParam = personCreditParam;
            this.personCredit = personCredit;
            this.xdoc = personCreditParam.getXdoc();
            List<CreditCard> list = getInfo();
            if (null != list) {
                personCredit.setCreditCardList(list);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("analyse", e);
            return false;
        }
    }

    public List<CreditCard> getInfo() {
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
        if (version == 0) {
            return setValue_detail();
        } else if (version == 1) {
            return setValue_2011();
        } else if (version == 2) {
            return setValue_2012();
        } else {
            return setValue_2011();
        }
    }

    //各版本取值
    private List<CreditCard> setValue_detail() {
        int parasSize = paras.size();
        for (int i = 0; i < parasSize; i++) {
            XWPFParagraph para = paras.get(i);
            String outline = TableUtil.text(para);
            String[] begin = {"机", "构"};
            String[] end1 = {"截", "至"};
            String[] end2 = {"己", "于"};
            if (OutlineUtil.contains(outline, begin)) {
                if (OutlineUtil.contains(outline, end1) || OutlineUtil.contains(outline, end2)) {
                    XWPFTable table = DocUtil.getTable(para, elements);
                    list.add(value_detail(outline, table));
                } else {
                    int indexParaInElements = elements.indexOf(para);
                    int indexParaInElementsNext = indexParaInElements + 1;
                    IBodyElement element = elements.get(indexParaInElementsNext);
                    if (element instanceof XWPFParagraph) {
                        int iNext = i + 1;
                        if (iNext < parasSize) {
                            XWPFParagraph paraNext = paras.get(iNext);
                            String outlineNext = TableUtil.text(paraNext);
                            if (!OutlineUtil.contains(outlineNext, begin)
                                    && (OutlineUtil.contains(outlineNext, end1) || OutlineUtil.contains(outlineNext, end2))) {
                                XWPFTable table = DocUtil.getTable(paraNext, elements);
                                list.add(value_detail(outline + outlineNext, table));
                                ++i;
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    private CreditCard value_detail(String outline, XWPFTable table) {
        CreditCard entity = new CreditCard();
        outline(entity, outline);

        if (null != table) {
            rows = table.getRows();
            try {
                if (rows.size() > 3) {
                    cells = rows.get(1).getTableCells();
                    entity.setAccountStatus(TableUtil.text(cells.get(0)));
                    entity.setConsumedCredit(TableUtil.text(cells.get(1)));
                    entity.setAverageConsumedCreditInRecent6Months(TableUtil.text(cells.get(2)));
                    entity.setMaximumUseCredit(TableUtil.text(cells.get(3)));
                    entity.setRepayableAmountOfTheMonth(TableUtil.text(cells.get(4)));
                    cells = rows.get(3).getTableCells();
                    entity.setStatementDate(TableUtil.text(cells.get(0)));
                    entity.setPaidInAmountOfTheMonth(TableUtil.text(cells.get(1)));
                    entity.setLatestRepaymentDate(TableUtil.text(cells.get(2)));
                    entity.setCurrentOverdueInstallment(TableUtil.text(cells.get(3)));
                    entity.setCurrentOverdueAmount(TableUtil.text(cells.get(4)));
                }
            } catch (Exception e) {
                log.error("value_2011", e);
            }
        }
        return entity;
    }

    private List<CreditCard> setValue_2011() {
        for (int i = 0; i < paras.size(); i++) {
            XWPFParagraph para = paras.get(i);
            String outline = TableUtil.text(para);
            if (TableUtil.text(para).length() < 61) {
                continue;
            }
            XWPFTable table = DocUtil.getTable(para, elements);
            list.add(value_2011(para, table));
        }
        return list;
    }

    private CreditCard value_2011(XWPFParagraph para, XWPFTable table) {
        CreditCard entity = new CreditCard();
        outline(entity, TableUtil.text(para));

        if (null != table) {
            rows = table.getRows();
            try {
                if (rows.size() > 3) {
                    cells = rows.get(1).getTableCells();
                    entity.setSharedCredit(TableUtil.text(cells.get(0)));
                    entity.setConsumedCredit(TableUtil.text(cells.get(1)));
                    entity.setAverageConsumedCreditInRecent6Months(TableUtil.text(cells.get(2)));
                    entity.setMaximumUseCredit(TableUtil.text(cells.get(3)));
                    entity.setRepayableAmountOfTheMonth(TableUtil.text(cells.get(4)));
                    cells = rows.get(3).getTableCells();
                    entity.setStatementDate(TableUtil.text(cells.get(0)));
                    entity.setPaidInAmountOfTheMonth(TableUtil.text(cells.get(1)));
                    entity.setLatestRepaymentDate(TableUtil.text(cells.get(2)));
                    entity.setCurrentOverdueInstallment(TableUtil.text(cells.get(3)));
                    entity.setCurrentOverdueAmount(TableUtil.text(cells.get(4)));
                }
            } catch (Exception e) {
                log.error("value_2011", e);
            }
        }
        return entity;
    }

    //概要
    private void outline(CreditCard entity, String outline) {
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

    private List<CreditCard> setValue_2012() {
        for (int i = 0; i < tables.size(); i++) {
            rows = tables.get(i).getRows();
            CreditCard entity = new CreditCard();
            try {
                if (rows.size() < 2)
                    break;
                cells = rows.get(1).getTableCells();
                entity.setCardIssuer(TableUtil.text(cells.get(0)));
                entity.setBusinessNo(TableUtil.text(cells.get(1)));
                entity.setCurrency(TableUtil.text(cells.get(2)));
                entity.setIssueDate(TableUtil.text(cells.get(3)));
                entity.setLineOfCredit(TableUtil.text(cells.get(4)));
                entity.setGuarantyStyle(TableUtil.text(cells.get(5)));
                if (rows.size() < 3)
                    break;
                cells = rows.get(2).getTableCells();
                entity.setAccountStatus(TableUtil.text(cells.get(1)));
                entity.setStateDeadlineDay(TableUtil.text(cells.get(3)));
                if (rows.size() < 5)
                    break;
                cells = rows.get(4).getTableCells();
                entity.setSharedCredit(TableUtil.text(cells.get(0)));
                entity.setConsumedCredit(TableUtil.text(cells.get(1)));
                entity.setAverageConsumedCreditInRecent6Months(TableUtil.text(cells.get(2)));
                entity.setMaximumUseCredit(TableUtil.text(cells.get(3)));
                entity.setRepayableAmountOfTheMonth(TableUtil.text(cells.get(4)));
                if (rows.size() < 7)
                    break;
                cells = rows.get(6).getTableCells();
                entity.setStatementDate(TableUtil.text(cells.get(0)));
                entity.setPaidInAmountOfTheMonth(TableUtil.text(cells.get(1)));
                entity.setLatestRepaymentDate(TableUtil.text(cells.get(2)));
                entity.setCurrentOverdueInstallment(TableUtil.text(cells.get(3)));
                entity.setCurrentOverdueAmount(TableUtil.text(cells.get(4)));
            } catch (Exception e) {
                log.error("CreditCardProcessor.setValue_2012", e);
            }
            list.add(entity);
        }
        return list;
    }

    final List<String> KeywordBegin = Arrays.asList("贷记卡", "（一）贷记卡", "（二）贷记卡");
    final List<String> KeywordEnd = Arrays.asList("准贷记卡", "（二）准贷记卡", "（三）准贷记卡"
            , "公共信息明细", "四公共信息明细"
            , "查询记录", "四查询记录", "五查询记录");
}
