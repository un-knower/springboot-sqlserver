package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.InquiryRecord;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.service.service.PersonCreditProcessor;
import com.yingu.project.util.utils.StringUtil;
import com.yingu.project.util.utils.TableUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class InquiryRecordProcessor extends PersonCreditProcessor {
    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.InquiryRecordProcessor.getValue();
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        try {
            List<InquiryRecord> list = getInfo(personCreditParam.getXdoc());
            if (null != list) {
                personCredit.setInquiryRecordList(list);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("InquiryRecordProcessor.analyse", e);
            return false;
        }
    }

    List<XWPFTable> tables;

    public List<InquiryRecord> getInfo(XWPFDocument xdoc) {
//        List<XWPFTable> tables = DocUtil.getTablesItem(KeywordBegin, KeywordEnd, xdoc);

        tables = xdoc.getTables();
        getFirstTable();
        getNextTables();

        //拼接tables
        XWPFTable tablePrimary = firstTable;
        if (nextTables != null) {
            for (int i = 0; i < nextTables.size(); i++) {
                XWPFTable table = nextTables.get(i);
                List<XWPFTableRow> rows = table.getRows();
                for (int j = 0; j < rows.size(); j++) {
                    tablePrimary.addRow(rows.get(j));
                }
            }
        }

        //分版本取值
        return setValue_detail(tablePrimary.getRows());
    }

    XWPFTable firstTable;
    int firstTableIndex;
    List<XWPFTable> nextTables;

    public void getFirstTable() {
        for (int i = 0; i < tables.size(); i++) {
            XWPFTable table = tables.get(i);
            //列数
            List<XWPFTableRow> rows = table.getRows();
            if (rows.size() < 1)
                continue;
            List<XWPFTableCell> cells = rows.get(0).getTableCells();
            if (cells.size() != CellSize)
                continue;
            //关键字
            if (TableUtil.determineType(table, Keyword)) {
                firstTable = table;
                firstTableIndex = i;
                return;
            }
        }
    }

    public void getNextTables() {
        for (int i = firstTableIndex; i < tables.size(); i++) {
            XWPFTable table = tables.get(i);
            //列数
            List<XWPFTableRow> rows = table.getRows();
            if (rows.size() < 1)
                return;
            List<XWPFTableCell> cells = rows.get(0).getTableCells();
            if (cells.size() != CellSize)
                return;
            //关键字
            if (!TableUtil.determineType(table, Keyword)) {
                if (nextTables == null)
                    nextTables = new ArrayList<>();
                nextTables.add(table);
            } else {
                return;
            }
        }
    }

    private List<InquiryRecord> setValue_detail(List<XWPFTableRow> rows) {
        List<InquiryRecord> list = new ArrayList<>();
        List<XWPFTableCell> cells;
        boolean flag = false;
        for (int i = 0; i < rows.size(); i++) {
            cells = rows.get(i).getTableCells();
            if (flag == false) {
                if (cells.size() == CellSize)
                    flag = true;
            } else {
                if (cells.size() != CellSize)
                    continue;
                InquiryRecord entity = new InquiryRecord();
                entity.setQueryDate(StringUtil.trim(cells.get(1).getText()));
                entity.setQueryOperator(StringUtil.trim(cells.get(2).getText()));
                entity.setQueryCause(StringUtil.trim(cells.get(3).getText()));
                list.add(entity);
            }
        }
        return list;
    }

    final List<String> KeywordBegin = Arrays.asList("四查询记录", "五查询记录"
            , "查询记录");
    final List<String> KeywordEnd = Arrays.asList("本人查询记录明细", "报告说明");
    final int CellSize = 4;
    final String Keyword = "查询操作员";
}
