package poi;

import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.api.Status;
import com.yingu.project.util.utils.StringUtil;
import com.yingu.project.util.utils.TableUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class TestXWPFDocument {
    final String WuJiFenLei = "五级分类";//五级分类，wujifenlei，三信贷交易信息明细，贷款，2+(8*n)+7
    //final String ChaXunJiLu = "查询记录";//四查询记录、五查询记录，chaxunjilu
    //final String BenRenChaXunJiLu = "本人查询记录";//本人查询记录明细，benrenchaxunjilu
//    final String[] ChaXunJiLu = {"四查询记录", "五查询记录"};//四查询记录、五查询记录，chaxunjilu
    final List<String> ChaXunJiLu = Arrays.asList("四查询记录", "五查询记录");//四查询记录、五查询记录，chaxunjilu
    final String BenRenChaXunJiLu = "本人查询记录明细";//本人查询记录明细，benrenchaxunjilu
    final String ChaXunJiLuHuiZong = "査询记录汇总";//査询记录汇总，chaxunjiluhuizong


    @Test
    public void testParagraph() {
        final String zhengJianHaoMa = "被査询者证件号码";//被査询者证件号码

        File file = new File("D:\\word\\001.docx");
        try {
            FileInputStream fis = new FileInputStream(file);
            XWPFDocument xdoc = new XWPFDocument(fis);
            //段落
            List<XWPFParagraph> paras = xdoc.getParagraphs();
            for (XWPFParagraph para : paras) {
//                System.out.println(para.getText());
                POIXMLDocumentPart part = para.getPart();
//                part.getRelationCounter();
                System.out.println(para.getText());
            }
            //表格
            List<XWPFTable> tables = xdoc.getTables();
            List<XWPFTableRow> rows;
            List<XWPFTableCell> cells;
            XWPFTableRow row;
            XWPFTableCell cell;
            for (XWPFTable table : tables) {
                rows = table.getRows();
                for (int i = 0; i < rows.size(); i++) {
                    row = rows.get(i);
                    cells = row.getTableCells();
                    for (int j = 0; j < cells.size(); j++) {
                        cell = cells.get(j);
                        String cellValue = cell.getText();
                        if (cellValue.equals(zhengJianHaoMa)) {
                            //读取下一行的同列单元格内容
                            log.info(zhengJianHaoMa + rows.get(i + 1).getCell(j).getText());
                        }
                    }
                }
            }
            fis.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testLoanInfo() {
        File file = new File("D:\\word\\001.docx");
        try {
            FileInputStream fis = new FileInputStream(file);
            XWPFDocument xdoc = new XWPFDocument(fis);
            List<XWPFTable> tables = xdoc.getTables();
            fis.close();
            tables = getTables(tables, WuJiFenLei);

            for (int i = 0; i < tables.size(); i++) {
                XWPFTable table = tables.get(i);
                validateTableLoan(table);
                System.out.printf("表格%s\n", (i + 1));
                outTable(table);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testIBodyElement() {
        File file;
        file = new File("D:\\word\\001.docx");
        file = new File("D:\\word\\2012.docx");
        file = new File("D:\\word\\2011-2.docx");
        file = new File("D:\\word\\2011-4.docx");
        XWPFDocument xdoc = new XWPFDocument();
        try {
            FileInputStream fis = new FileInputStream(file);
            xdoc = new XWPFDocument(fis);
            fis.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (null == xdoc)
            return;

        List<IBodyElement> elements = xdoc.getBodyElements();
        for (int i = 0; i < elements.size(); i++) {
            IBodyElement element = elements.get(i);
            if (i == 54) {
                int aa = 0;
            }
            if (element instanceof XWPFParagraph) {
                System.out.printf("---------------------------------段落%s--------------------------------------\n", (i + 1));
                XWPFParagraph para = (XWPFParagraph) element;
                System.out.println(para.getText());
//                System.out.println(TableUtil.typo(para.getText()));
            } else if (element instanceof XWPFTable) {
                System.out.printf("---------------------------------表格%s\n", (i + 1));
                outTable((XWPFTable) element);
            }
        }
    }

    @Test
    public void testInquiryRecord() {
        XWPFDocument xdoc = xdoc();
//        List<String> itemBegin = ChaXunJiLu;
//        String itemEnd = BenRenChaXunJiLu;
        List<XWPFTable> tables = getTablesItem(ChaXunJiLu, BenRenChaXunJiLu, xdoc);
        tables = getTablesItem(ChaXunJiLuHuiZong, tables);
        for (int i = 0; i < tables.size(); i++) {
            System.out.printf("---------------------------------表格%s\n", (i + 1));
            outTable(tables.get(i));
        }
    }

    @Test
    public void doc2Elements(){
        XWPFDocument xdoc = xdoc();
        TableUtil.doc2Elements(xdoc);
    }

    //验证表格
    public boolean validateTableLoan(XWPFTable table) {
        //2+(8*n)+7
        int rowsSize = table.getRows().size();
        int remainder = (rowsSize - 9) % 8;
        if (remainder == 0)
            return true;
        return false;
    }

    //输出表格
    public void outTable(XWPFTable table) {
        List<XWPFTableRow> rows = table.getRows();
        List<XWPFTableCell> cells;
        XWPFTableRow row;
        XWPFTableCell cell;
        String cellText;
        for (int i = 0; i < rows.size(); i++) {
            System.out.printf("第%s行\t", i + 1);
            row = rows.get(i);
            cells = row.getTableCells();
            for (int j = 0; j < cells.size(); j++) {
                cell = cells.get(j);
                cellText = cell.getText();
                System.out.printf("%s\t", cellText);
            }
            System.out.print("\n");
        }
    }

    //获取某种类型的tables
    public static List<XWPFTable> getTables(List<XWPFTable> tables, String key) {
        List<XWPFTable> tablesClassified = new ArrayList<>();
        for (XWPFTable table : tables) {
            if (determineType(table, key))
                tablesClassified.add(table);
        }
        return tablesClassified;
    }

    //获取某项信息的全部表格
    public static List<XWPFTable> getTablesItem(String itemBegin, String itemEnd, XWPFDocument xdoc) {
        List<IBodyElement> elements = xdoc.getBodyElements();
        //找起止index
        int indexBegin = 0;
        int indexEnd = 0;
        boolean flag = true;
        for (int i = 0; i < elements.size(); i++) {
            IBodyElement element = elements.get(i);
            if (element instanceof XWPFParagraph) {
                XWPFParagraph para = (XWPFParagraph) element;
                String paraText = para.getText();
                if (flag && paraText.indexOf(itemBegin) != -1) {
                    indexBegin = i;
                    flag = false;
                } else if (paraText.indexOf(itemEnd) != -1) {
                    indexEnd = i;
                    break;
                }
            } else if (element instanceof XWPFTable) {

            }
        }
        //获取信息，by起止index
        List<XWPFTable> tables = new ArrayList<>();
        for (int i = indexBegin + 1; i < indexEnd; i++) {
            IBodyElement element = elements.get(i);
            if (element instanceof XWPFTable) {
                tables.add((XWPFTable) element);
            }
        }
        return tables;
    }

    public static List<XWPFTable> getTablesItem(List<String> itemBegin, String itemEnd, XWPFDocument xdoc) {
        List<IBodyElement> elements = xdoc.getBodyElements();
        //找起止index
        int indexBegin = 0;
        int indexEnd = 0;
        boolean flag = true;
        for (int i = 0; i < elements.size(); i++) {
            IBodyElement element = elements.get(i);
            if (element instanceof XWPFParagraph) {
                XWPFParagraph para = (XWPFParagraph) element;
                String paraText = para.getText();
                if (flag && itemBegin.contains(paraText)) {
                    indexBegin = i;
                    flag = false;
                } else if (itemEnd.equals(paraText)) {
                    indexEnd = i;
                    break;
                }
            } else if (element instanceof XWPFTable) {

            }
        }
        //获取信息，by起止index
        List<XWPFTable> tables = new ArrayList<>();
        for (int i = indexBegin + 1; i < indexEnd; i++) {
            IBodyElement element = elements.get(i);
            if (element instanceof XWPFTable) {
                tables.add((XWPFTable) element);
            }
        }
        return tables;
    }

    public static List<XWPFTable> getTablesItem(String itemBegin, List<XWPFTable> tables) {
        //找起止index
        int indexBegin = 0;
        int size = tables.size();
        for (int i = 0; i < size; i++) {
            XWPFTable table = tables.get(i);
            if (determineType(table, itemBegin)) {
                indexBegin = i;
                break;
            }
        }
        //获取信息，by起止index
        List<XWPFTable> tablesClassified = new ArrayList<>();
        for (int i = indexBegin + 1; i < size; i++) {
            XWPFTable table = tables.get(i);
            tablesClassified.add(table);
        }
        return tablesClassified;
    }

    //判断table的类型
    public static boolean determineType(XWPFTable table, String key) {
        if (null == table || null == key) {
            return false;
        }
        List<XWPFTableRow> rows = table.getRows();
        if (rows == null || rows.size() == 0) {
            return false;
        }
        String cellValue;
        for (XWPFTableRow row : rows) {
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {
                cellValue = StringUtil.trim(cell.getText());
                if (key.equals(cellValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    //xdoc
    public XWPFDocument xdoc() {
        File file = new File("D:\\word\\001.docx");
//        File file = new File("D:\\word\\2012.docx");
        XWPFDocument xdoc = new XWPFDocument();
        try {
            FileInputStream fis = new FileInputStream(file);
            xdoc = new XWPFDocument(fis);
            fis.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
//        if (null == xdoc)
        return xdoc;
    }
}
