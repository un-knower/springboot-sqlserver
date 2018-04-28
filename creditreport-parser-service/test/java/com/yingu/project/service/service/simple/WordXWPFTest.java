//package com.yingu.project.service.service.simple;
//
//import com.yingu.project.service.service.simple.xdoc.HeaderPhService;
//import com.yingu.project.service.service.simple.config.Config;
//import com.yingu.project.service.service.simple.lucene.LuceneHelper;
//import com.yingu.project.util.utils.StringUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.search.*;
//import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import org.apache.poi.xwpf.usermodel.XWPFParagraph;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * doc文件的读取，需要导入poi-scratchpad的jar包
// * 1.1     通过WordExtractor读文件(在WordExtractor内部进行信息读取时还是通过HWPFDocument来获取的。)
// * 1.2     通过HWPFDocument读文件
// * docx文件读取，需要导入poi-ooxml的jar包
// * 1.1      通过XWPFDocument读取文件
// * Paragraph：word文档的一个段落，一个小节可以由多个段落构成。
// * CharacterRun：具有相同属性的一段文本，一个段落可以由多个CharacterRun组成。
// * Table：一个表格。
// * TableRow：表格对应的行。
// * TableCell：表格对应的单元格。
// *
// * @Date: Created in 2018/3/27 17:38
// * @Author: wm
// */
//@Slf4j
//public class WordXWPFTest {
//    public String wordPath = "D:/word/doc_6_basic.docx";
//    LuceneHelper indexer;
//
//    FileInputStream fis;
//    XWPFDocument xdoc;
//
//    @Before
//    public void setUp() throws Exception {
//        indexer = LuceneHelper.getInstance();
//        indexer.deleteAll();
//
//        File file = new File(wordPath);
//        fis = new FileInputStream(file);
//        xdoc = new XWPFDocument(fis);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        indexer.deleteAll();
//        indexer.close();
//
//        fis.close();
//    }
//
//    @Test
//    public void readAndWriterTest4() throws Exception {
//        HeaderPhService paragraph = new HeaderPhService();
//        //段落提取
//        XWPFParagraph headerPh = paragraph.getParagraph(xdoc);
//
//        if (null == headerPh) {
//            log.error("段落 HeaderPh 解析失败。" + wordPath);
//        } else {
//
//        }
//
//        System.out.println("\n\nheaderPh\n" + headerPh.getText());
//        indexer.deleteAll();
//        //缓存runs, 包括换行拆分的runs
//        List<String> runList = new ArrayList<String>();
//        int runNum = 0;
//        for (int i = 0; i < headerPh.getRuns().size(); i++) {
//            String item = headerPh.getRuns().get(i).getPictureText();
//            if (StringUtil.isEmpty(item)) {
//                continue;
//            }
//            String[] subItems = StringUtils.split(item, System.lineSeparator());
//            if (subItems.length > 1) {
//                for (String subItem : subItems) {
//                    if (StringUtil.isEmpty(subItem)) {
//                        continue;
//                    }
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put(Config.searchField, subItem);
//                    map.put(Config.indexField, i + "");
//                    map.put(Config.runIndexField, runNum + "");
//                    indexer.addDocument(map);
//                    runList.add(subItem);
//                    runNum++;
//                }
//            } else {
//                Map<String, String> map = new HashMap<String, String>();
//                map.put(Config.searchField, item);
//                map.put(Config.indexField, i + "");
//                map.put(Config.runIndexField, runNum + "");
//                indexer.addDocument(map);
//                runList.add(item);
//                runNum++;
//            }
//        }
//        indexer.closeWriter();
//
//        //报告编号
//        String contractNo = null;
//        String fieldName = "报告编号";
//        Integer runIndex = null;
//        Integer contractNoRunIndex = null;
//        System.out.println("》》find field keyword=".concat(fieldName));
//        Term t = new Term(Config.searchField, fieldName);
//        FuzzyQuery query = new FuzzyQuery(t);
//        BoostQuery boostQuery = new BoostQuery(query, 0.8f);
//        IndexSearcher indexSearcher = indexer.getIndexSearcher();
//        TopDocs hits = indexSearcher.search(boostQuery, Integer.MAX_VALUE);
//        if (hits.totalHits > 0) {
////            System.out.println("总共查询到" + hits.totalHits + "个文档");
////            int j = 0;
////            for (ScoreDoc scoreDoc : hits.scoreDocs) {
////                Document doc = indexSearcher.doc(scoreDoc.doc);
////                String index = doc.get(Config.indexField);
////                String runIndex = doc.get(Config.runIndexField);
////                System.out.println("《字段 匹配结果   index=" + index + "\t runIndex=" + runIndex + "\t score=" + scoreDoc.score + "\t" + doc.get(Config.searchField));
////                j++;
////            }
//            //固定 取第一个
//            ScoreDoc scoreDoc = hits.scoreDocs[0];
//            Document doc = indexSearcher.doc(scoreDoc.doc);
//            String index = doc.get(Config.indexField);
//            runIndex = Integer.parseInt(index);
//            String runIndexStr = doc.get(Config.runIndexField);
//            System.out.println("《字段 匹配结果   index=" + index + "\t runIndex=" + runIndexStr + "\t score=" + scoreDoc.score + "\t" + doc.get(Config.searchField));
//            contractNoRunIndex = Integer.parseInt(doc.get(Config.runIndexField));
//            if (runList.size() > contractNoRunIndex + 1) {
//                //固定 取下一个runIndex值，依赖有空格的前提
//                contractNo = runList.get(contractNoRunIndex + 1).trim();
//                if (StringUtil.isEmpty(contractNo)|| contractNo.length()<1){
//                    contractNo = null;
//                }
//            }
//        }
//        if (null == contractNo) {
//            log.error("字段 " + fieldName + " 解析失败。" + wordPath);
//        }
//        System.out.println("《《字段 匹配结果 " + fieldName + "=" + contractNo);
//
//        //姓名
//        String clientName = null;
//        //获取所在标签run
//        //固定 姓名和报告编号在一个标签内来限定姓名是结尾
//        String runStr = headerPh.getRuns().get(runIndex).getPictureText();
//        //删除空格和换行
//        runStr = StringUtils.deleteWhitespace(runStr);
//        //全角替换
//        runStr = StringUtils.replaceAll(runStr, "：", ":");
//        //固定 有“名”，且姓名之后没有字符
//        if (runStr.contains("名:")) {
//            clientName = StringUtils.substringAfter(runStr, "名:");
//        }else if(runStr.contains("名")){
//            clientName = StringUtils.substringAfter(runStr, "名");
//        }
//        if (StringUtil.isEmpty(clientName)|| clientName.length()<1){
//            clientName = null;
//        }
//
//        indexer.closeSearcher();
//        indexer.deleteAll();
//
//    }
//}
