package com.yingu.project.service.service.simple;

import com.alibaba.fastjson.JSON;
import com.yingu.project.rest.app.Application;
import com.yingu.project.service.service.simple.impl.ExtractInfoSimilarityServiceImpl;
import com.yingu.project.service.service.simple.xdoc.DocElement;
import com.yingu.project.service.service.simple.xdoc.SimilarityKeyword;
import com.yingu.project.service.service.simple.xdoc.XdocService;
import com.yingu.project.service.service.simple.xdoc.XdocUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 简版解析器
 * 前提：
 * ·每个文件只包含一个人的报告
 * ·每个报告只有一种模板类型
 *
 * @Date: Created in 2018/3/23 14:37
 * @Author: wm
 */
//@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class WordTest {
    @Autowired
    XdocService xdocService;

    @Test
    public void split(){
        String[] strArr = StringUtils.split("abc", System.lineSeparator());
        System.out.println(strArr);
    }

    @Test
    public void error() {
        Exception e1 = new Exception("e1 error");
        Exception e2 = new Exception("e2 error", e1);
        Exception e3 = new Exception("e3 error", e2);
        StringBuilder errorMs = new StringBuilder(e3.getMessage());
        Throwable tempEx = e3;
        for (int i = 0; i < 10; i++) {
            if (tempEx.getCause() == null) {
                break;
            } else {
                tempEx = tempEx.getCause();
                errorMs.append("\n").append(tempEx.getMessage());
            }
        }
        System.out.println(errorMs);
    }

    @Test
    public void fuzzyContains() {
        String sourceStr = "1本报吿不展示5年前已经结柬的逾期及违约行为，以及5年前的欠税记录、强制执行记录、民事判决记录、行政处罚 记";
        String keyword = "为他人担保笔数";
        boolean isC = XdocUtil.fuzzyContains(sourceStr, new SimilarityKeyword(keyword, 66));
        System.out.println(isC);
    }

    @Test
    public void XdocUtil_substringFuzzyBetween() {
        //正常
//        String str = "执行法院：重庆市合川区人民法院 案号：(2015)合法行非执字第01124号";
        //开头X
//        String str = "X行法院：重庆市合川区人民法院 X号：(2015)合法行非执字第01124号";
//        String str = "XX行法院：重庆市合川区人民法院 X号：(2015)合法行非执字第01124号";
        //中间X
//        String str = "执行X院：重庆市合川区人民法院 案X：(2015)合法行非执字第01124号";
//        String str = "执行法X：重庆市合川区人民法院 案X：(2015)合法行非执字第01124号";
        String str = "执行法XX：重庆市合川区人民法院 案XX：(2015)合法行非执字第01124号";
        //尾部X
//        String str = "执行法院X重庆市合川区人民法院 案号X(2015)合法行非执字第01124号";
//        String str = "执行法院XX重庆市合川区人民法院 案号XX(2015)合法行非执字第01124号";

        String result = XdocUtil.substringFuzzyBetween(str, new SimilarityKeyword("执行法院：", 60), new SimilarityKeyword(" 案号：", 66));
        System.out.println(result);
        //冒号未识别出来
        //中间的X未识别出来
        //List<MatchInfo> 取得了多个，匹配过的字符就可以跳过了。比如“执行法院”“行法院”
    }

    @Test
    public void tt() {
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        System.out.println(JSON.toJSONString(list.subList(1, 5)));
    }

    @Test
    public void resolver() {
        String filePath = "D:/word/DDPHYB1642017120900002.docx";
        File file = new File(filePath);

        try {
            FileInputStream fis = new FileInputStream(file);
            XWPFDocument xdoc = new XWPFDocument(fis);

//            System.out.println("\n\n------------------------getBodyElements start");
//            int startIndex = 0;//不包含
//            int endIndex = 0;//包含
//            for (int j=0;j<xdoc.getBodyElements().size();j++) {
//                IBodyElement iBodyElement = xdoc.getBodyElements().get(j);
//                System.out.println("\n--------iBodyElement " + j);
//                System.out.println("--------iBodyElement.getElementType() " + iBodyElement.getElementType());
//                if (iBodyElement instanceof XWPFParagraph) {
//                    XWPFParagraph paragraph = (XWPFParagraph) iBodyElement;
//                    if (paragraph.getText().contains("发生过逾期的贷记卡账户明细如下")) {
//                        startIndex = j;
//                    }
//                    if (startIndex > 0){
//                        System.out.println(paragraph.getText());
//                    }
//                    if (startIndex > 0 && startIndex!=j && (!paragraph.getText().contains("。") && !paragraph.getText().contains("，"))) {
//                        endIndex = j;
//                        break;
//                    }
//                } else if (iBodyElement instanceof XWPFTable) {
//                    XWPFTable table = (XWPFTable) iBodyElement;
////                    System.out.println(table.getText());
//                }
//            }
//            System.out.println("startIndex="+startIndex);
//            System.out.println("endIndex="+endIndex);
//            System.out.println("\n------------------------getBodyElements end\n\n");

            //读取段落,不包含表格
            List<XWPFParagraph> paras = xdoc.getParagraphs();
            System.out.println("\n\n------------------------XWPFParagraph start");
            int i = 0;
            for (XWPFParagraph para : paras) {
                System.out.println("\n--------paragraph " + i);
                System.out.println(para.getText());
                i++;
            }
            System.out.println("------------------------XWPFParagraph end\n\n");

            //获取文档中所有的表格
//            System.out.println("\n\n------------------------XWPFTable start");
//            List<XWPFTable> tables = xdoc.getTables();
//            List<XWPFTableRow> rows;
//            List<XWPFTableCell> cells;
//            XWPFTableRow row;
//            XWPFTableCell cell;
//            int index=0;
//            for (XWPFTable table : tables) {
//                System.out.println("\n------table "+index++);
//                rows = table.getRows();
//                for (int i = 0; i < rows.size(); i++) {
//                    System.out.println("\n------row "+i);
//                    row = rows.get(i);
//                    cells = row.getTableCells();
//                    for (int j = 0; j < cells.size(); j++) {
//                        cell = cells.get(j);
//                        String cellValue = cell.getText();
//                        System.out.print(cellValue + "\t");
////                            if(cellValue.equals("被査询者证件号码")){
////                                //读取下一行的同列单元格内容
////                                log.info("被査询者证件号码:"+rows.get(i+1).getCell(j).getText());
////
////                            }
//                    }
//                }
//            }
//            System.out.println("------------------------XWPFTable end\n\n");
            fis.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
