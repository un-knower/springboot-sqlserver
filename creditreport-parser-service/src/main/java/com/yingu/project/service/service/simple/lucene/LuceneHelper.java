package com.yingu.project.service.service.simple.lucene;


import com.alibaba.fastjson.JSON;
import com.yingu.project.service.service.simple.config.Config;
import com.yingu.project.service.service.simple.xdoc.DocElement;
import com.yingu.project.util.utils.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yingu.project.service.service.simple.config.Config.searchField;

/**
 * @Date: Created in 2018/3/27 15:04
 * @Author: wm
 */
@Slf4j
public class LuceneHelper {
    private LuceneHelper() {
    }

    private static LuceneHelper indexer;

    @SneakyThrows
    public static LuceneHelper getInstance() {
        if (null == indexer) {
            indexer = new LuceneHelper();
            indexer.indexPath = Config.indexPath;
            indexer.dir = FSDirectory.open(Paths.get(indexer.indexPath));
        }
        return indexer;
    }

    private String indexPath;
    private Directory dir;
    private IndexWriter writer; // 写索引实例

    private IndexReader reader;

    private List<DocElement> docElementList;

    /**
     * 查询第一个相似run(显示块)
     *
     * @param runList
     * @param headerPh
     * @param fieldName
     * @return
     */
    @SneakyThrows
    public DocElement indexSearch(List<String> runList, XWPFParagraph headerPh, String fieldName) {
        DocElement runVo = null;
        LuceneHelper indexer = LuceneHelper.getInstance();
        Integer runIndex = null;
        Integer contractNoRunIndex = null;
        log.info("》》find field keyword=".concat(fieldName));
        Term t = new Term(searchField, fieldName);
        FuzzyQuery query = new FuzzyQuery(t);
//            BoostQuery boostQuery = new BoostQuery(query, 0.8f);
        IndexSearcher indexSearcher = indexer.getIndexSearcher();
        TopDocs hits = indexSearcher.search(query, Integer.MAX_VALUE);
        if (hits.totalHits > 0) {
//            log.info("总共查询到" + hits.totalHits + "个文档");
//            int j = 0;
//            for (ScoreDoc scoreDoc : hits.scoreDocs) {
//                Document doc = indexSearcher.doc(scoreDoc.doc);
//                String index = doc.get(Config.indexField);
//                String runIndex = doc.get(Config.runIndexField);
//                log.info("《字段 匹配结果   index=" + index + "\t runIndex=" + runIndex + "\t score=" + scoreDoc.score + "\t" + doc.get(Config.searchField));
//                j++;
//            }
            //固定 取第一个
            ScoreDoc scoreDoc = hits.scoreDocs[0];
            Document doc = indexSearcher.doc(scoreDoc.doc);
            String index = doc.get(Config.indexField);
            runIndex = Integer.parseInt(index);
            String runIndexStr = doc.get(Config.runIndexField);
            log.info("《字段 匹配结果   index=" + index + "\t runIndex=" + runIndexStr + "\t score=" + scoreDoc.score + "\t" + doc.get(searchField));
            contractNoRunIndex = Integer.parseInt(doc.get(Config.runIndexField));
            if (runList.size() > contractNoRunIndex + 1) {
//                //固定 取下一个runIndex值，依赖有空格的前提
//                String value = StringUtils.deleteWhitespace(runList.get(contractNoRunIndex + 1));
                runVo = new DocElement();
//                runVo.setValue(value);
                runVo.setIndex(runIndex);
                //获取所在标签run
                String runStr = headerPh.getRuns().get(runVo.getIndex()).getPictureText();
                //验证
                if (StringUtil.isEmpty(runStr)) {
                    runStr = null;
                }
                log.info("《《字段 匹配run结果 " + fieldName + "=" + runStr);
                runVo.setValue(runStr);
            }
        }
        return runVo;

//        text = StringUtils.replaceAll(text, "：", ":");
//
//        //获取头部信息，将其去掉空格，根据关键字取其中的值
//        var header = StringUtils.substringBefore(text, "信贷记录");
//        header = StringUtils.deleteWhitespace(header);
//
//        //报告编号
//        var contractNo = StringUtils.substringBetween(header, "报告编号:", "姓名:");
//        //查询时间，査!=查
//        var inquiryDateTime = StringUtils.substringBetween(header, "査询时间:", "证件类型");
//        //报告时间
//        var generationDateTime = StringUtils.substringBetween(header, "报告时间:", "证件号码");
//        //姓名
//        var clientName = StringUtils.substringBetween(header, "姓名:", "报告时间");
//        //证件类型
//        var cardType = StringUtils.substringBetween(header, "证件类型:", "报告编号");
//        //证件号码+是否已婚
//        var cardNoAndIsMarried = StringUtils.substringBetween(header, "证件号码:", "#信息概要");
//        //证件号码
//        var cardNo = cardNoAndIsMarried.substring(0, cardNoAndIsMarried.length()-2);
//        //是否已婚
//        var isMarried = StringUtils.substring(cardNoAndIsMarried, cardNoAndIsMarried.length() - 2);
//
//        var inquiryDateTimeObj = inputSdf.get().parse(inquiryDateTime);
//        var generationDateTimeObj = inputSdf.get().parse(generationDateTime);
//
//        var creditInfo = StringUtils.substringBetween(text, "# 信 息 概 要", "# 信 用 卡");
//        var assetsDispositionInfo = "";
//        if (StringUtils.contains(creditInfo, "资产处置信息")) {
//            var creditInfoArray = StringUtils.split(creditInfo, System.lineSeparator());
//            var creditInfoList = Arrays.asList(creditInfoArray);
//            var creditAmountArray = StringUtils.split(creditInfoList.get(2), " ");
//            var creditAmountList = Arrays.asList(creditAmountArray);
//            assetsDispositionInfo = creditAmountList.get(1);
//        }
//
//        resultMap.put("contractNo", contractNo);
//        resultMap.put("inquiryDateTime", outputSdf.get().format(inquiryDateTimeObj).toString());
//        resultMap.put("generationDateTime", outputSdf.get().format(generationDateTimeObj).toString());
//        resultMap.put("clientName", clientName);
//        resultMap.put("cardType", cardType);
//        resultMap.put("cardNo", cardNo);
//        resultMap.put("isMarried", isMarried);
//        resultMap.put("assetsDispositionInfo", assetsDispositionInfo);
    }

    /**
     * 添加记录
     * 注意：需手动关闭writer
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    @SneakyThrows
    public int addDocument(String key, String value) {
        int ef = 0;
        writer = getIndexWriter();

        try {
            System.out.println("add doc " + key + ":" + value);
            Document textDoc = new Document();
            textDoc.add(new TextField("text", value, Field.Store.YES));
            long num = writer.addDocument(textDoc);
            Assert.isTrue(num > 0, "lucene save result num=" + num);
            ef = writer.numDocs();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            writer.close();
        }
        return ef;
    }

    @SneakyThrows
    public IndexWriter getIndexWriter() {
        if (null == writer || !writer.isOpen()) {
            // 中文分词器
            Analyzer analyzer = new SmartChineseAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            writer = new IndexWriter(this.dir, iwc);
        }
        return writer;
    }

    /**
     * 注意：需手动关闭writer
     *
     * @param map
     * @throws Exception
     */
    @SneakyThrows
    public void addDocument(Map<String, String> map) {
        writer = getIndexWriter();
        try {
            System.out.println("add doc " + JSON.toJSONString(map));
            Document textDoc = new Document();
            for (String key : map.keySet()) {
                String value = map.get(key);
                StringField textField = new StringField(key, value, Field.Store.YES);
                textDoc.add(textField);
            }
            long num = writer.addDocument(textDoc);
            Assert.isTrue(num > 0, "lucene save result num=" + num);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            writer.close();
        }
    }

    @SneakyThrows
    public void closeWriter() {
        if (null != writer) {
            this.writer.close();
        }
    }

    /**
     * 清理所有索引库
     *
     * @throws IOException
     */
    @SneakyThrows
    public void deleteAll() {
        this.docElementList=null;
        writer = getIndexWriter();
        writer.deleteAll();
        this.closeWriter();
    }


    /**
     * 关闭写索引
     *
     * @throws Exception
     */
    public void close() throws Exception {
        if (null != dir) {
            this.dir.close();
        }
    }

    /**
     * 注意：需手动调closeSearcher关闭reader
     *
     * @return
     * @throws IOException
     */
    @SneakyThrows
    public IndexSearcher getIndexSearcher() {
        Assert.notNull(dir, "Directory is null");
        reader = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(reader);
        return is;
    }

    @SneakyThrows
    public void closeSearcher() {
        if (null != reader) {
            reader.close();
        }
    }

    /**
     * 索引runList
     *
     * @return runList
     */
    public void indexSave(List<DocElement> list) {
        this.docElementList=list;
        LuceneHelper indexer = LuceneHelper.getInstance();
        for (int i = 0; i < list.size(); i++) {
            DocElement docElement = list.get(i);
            Map<String, String> map = new HashMap<String, String>();
            map.put(searchField, docElement.getValue());
            map.put(Config.indexField, docElement.getIndex() + "");
            indexer.addDocument(map);
        }
        indexer.closeWriter();
    }

    /**
     * 获取匹配的列表
     * @param similarityKeywords
     * @return
     */
    public List<DocElement> indexSearchMatchList(String... similarityKeywords) {
        if (similarityKeywords.length == 0) {
            return null;
        }
        LuceneHelper luceneHelper = LuceneHelper.getInstance();

        List<DocElement> matchList = new ArrayList<DocElement>();
        //循环判断关键词是否存在
        IndexSearcher indexSearcher = luceneHelper.getIndexSearcher();
        indexSearcher.setSimilarity(new BooleanSimilarity());
        Assert.notNull(similarityKeywords, "similarityKeywords is null");
        try {
            for (String keyword : similarityKeywords) {
                System.out.println("》》find keyword=".concat(keyword));
                Term t = new Term(searchField, keyword);

                FuzzyQuery query = new FuzzyQuery(t);
                BoostQuery boostQuery = new BoostQuery(query, 100f);
//                SortField searchField = new SortField(Config.searchField, SortField.Type.STRING);
//                SortField sortDoc = new SortField(Config.searchField, SortField.Type.DOC, true);
                Sort sort = new Sort(SortField.FIELD_SCORE);
                TopDocs hits = indexSearcher.search(boostQuery, 10, sort, true, false);
                if (hits.totalHits > 0) {
                    System.out.println("总共查询到" + hits.totalHits + "个文档");
                    for (ScoreDoc scoreDoc : hits.scoreDocs) {
                        if (scoreDoc.score<65){
                            continue;
                        }
                        Document doc = indexSearcher.doc(scoreDoc.doc);
                        String indexStr = doc.get(Config.indexField);
                        Integer index = Integer.parseInt(indexStr);
                        String value = doc.get(searchField);
                        System.out.println("《文本匹配结果   index=" + index + "\t score=" + scoreDoc.score + "\t" + value);
                        matchList.add(new DocElement(index, value, scoreDoc.score));
                    }
                }
            }
        } catch (Exception e) {
            log.error("lucene search error", e);
        } finally {
            //判断一个段落后清除index
            luceneHelper.closeSearcher();
        }
        return matchList;
    }

    /**
     * 根据多个关键字相似查询，若多数匹配则返回index
     * 注意：index必须相同
     *
     * @param similarityKeywords 关键词 Array【必填
     * @return
     */
    public Integer indexSearch(String... similarityKeywords) {
        List<DocElement> matchList = this.indexSearchMatchList(similarityKeywords);
        if (0==matchList.size()){
            //when a lucene search failed ,a custom match is used.
            matchList = this.customMatchSearch(similarityKeywords);
        }

        if (null!= matchList && matchList.size() > 0) {
            //根据index分组
            Map<String, Object> map = new HashMap<String, Object>();
            for (DocElement docElement : matchList) {
                String countKey = docElement.getIndex().toString();
                if (!map.containsKey(countKey)) {
                    map.put(countKey, 1);
                } else {
                    int indexCount = (int) map.get(countKey);
                    map.put(countKey, indexCount + 1);
                }
            }
            //取得匹配次数最多的docElement
            Integer maxCount = 0;
            Integer index = null;
            for (String key : map.keySet()) {
                int count = (int) map.get(key);
                if (count > maxCount) {
                    maxCount = count;
                    index = Integer.parseInt(key);
                }
            }
            //若超过50%的存在则认为正确,只取第一次匹配成功的结果
            BigDecimal d1 = new BigDecimal(maxCount);
            BigDecimal d2 = new BigDecimal(similarityKeywords.length);
            BigDecimal rate = d1.divide(d2, 2, BigDecimal.ROUND_HALF_UP);
            if (rate.compareTo(BigDecimal.valueOf(0.5)) >= 0) {
                System.out.println("《《match index="+index);
                return index;
            }
        }
        return null;
    }

    private List<DocElement> customMatchSearch(String... similarityKeywords) {
        List<DocElement> list = new ArrayList<>();
        for (int i=0;i<this.docElementList.size();i++){
            DocElement docElement = this.docElementList.get(i);
            for (String keyword: similarityKeywords){
                if (StringUtil.isSimilar(keyword, docElement.getValue(), 7)){
                    list.add(docElement);
                }
            }
        }
        return list;
    }


    /**
     * 索引指定文件
     *
     * @param f
     */
//    private void indexFile(File f) throws Exception {
//        System.out.println("索引文件：" + f.getCanonicalPath());
//        Document doc = getDocument(f);
//        writer.addDocument(doc);
//    }

    /**
     * 获取文档，文档里再设置每个字段
     *
     * @param f
     */
//   private Document getDocument(File f) throws Exception {
//        Document doc = new Document();
//        doc.add(new TextField("contents", new FileReader(f)));
//        doc.add(new TextField("fileName", f.getName(), Field.Store.YES));
//        doc.add(new TextField("fullPath", f.getCanonicalPath(), Field.Store.YES));
//        return doc;
//    }
}
