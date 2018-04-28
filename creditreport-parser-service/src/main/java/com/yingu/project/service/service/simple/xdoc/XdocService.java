package com.yingu.project.service.service.simple.xdoc;

import com.yingu.project.api.SimilarInfo;
import com.yingu.project.util.utils.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * xdoc段落、run、表格处理
 *
 * @Date: Created in 2018/3/28 13:44
 * @Author: wm
 */
@Service
@Slf4j
public class XdocService {

    /**
     * 获取段落 list
     * 注意：
     * · 跳过empty的字符串
     * · 根据换行再次分割
     *
     * @param xdoc
     * @return
     */
    public List<DocElement> getDocElementList(XWPFDocument xdoc) {
        List<DocElement> list = new ArrayList<DocElement>();
        for (int i = 0; i < xdoc.getParagraphs().size(); i++) {
            XWPFParagraph paragraph = xdoc.getParagraphArray(i);
            String item = paragraph.getText();
            if (StringUtil.isEmpty(item)) {
                continue;
            }
            //每个段落的文本按换行分割后存lucene，记录段落编号
            String[] paragraphs = StringUtils.split(item, System.lineSeparator());
            if (paragraphs.length == 0) {
                list.add(new DocElement(i, item));
            } else {
                for (String subItem : paragraphs) {
                    if (StringUtil.isEmpty(subItem)) {
                        continue;
                    }
                    list.add(new DocElement(i, subItem));
                }
            }
        }
        return list;
    }

    /**
     * @param xdoc
     * @param similarityKeywords 被包含的关键词    默认66分
     * @return
     */
    public XWPFParagraph getParagraph(XWPFDocument xdoc, String[] similarityKeywords) {
        if (null == xdoc || similarityKeywords == null || similarityKeywords.length == 0) {
            return null;
        }

        List<SimilarityKeyword> similarityKeywordList = new ArrayList<>();
        for (String similarityKeyword : similarityKeywords) {
            similarityKeywordList.add(new SimilarityKeyword(similarityKeyword, 66));
        }

        return this.getParagraph(xdoc, similarityKeywordList);
    }

    @SneakyThrows
    private XWPFParagraph getParagraph(XWPFDocument xdoc, List<SimilarityKeyword> similarityKeywordList) {
        if (null == xdoc || similarityKeywordList == null || similarityKeywordList.size() == 0) {
            return null;
        }

        XWPFParagraph xwpfParagraph = null;
        List<DocElement> docElementList = this.getParagraphUnSpaceList(xdoc);

        DocElement docElement = this.containsSearch(docElementList, similarityKeywordList);
        if (null != docElement && null != docElement.getIndex()) {
            xwpfParagraph = xdoc.getParagraphArray(docElement.getIndex());
        }
        return xwpfParagraph;
    }

    /**
     * @param docElementList
     * @param similarityKeywordList
     * @return 关键词匹配最多的DocElement
     */
    public DocElement containsSearch(List<DocElement> docElementList, List<SimilarityKeyword> similarityKeywordList) {
        if (docElementList == null || docElementList.size() == 0 || similarityKeywordList == null || similarityKeywordList.size() == 0) {
            return null;
        }
        List<DocElement> allMatchList = new ArrayList<>();
        //每个关键词查询
        for (SimilarityKeyword similarityKeyword : similarityKeywordList) {
            List<DocElement> matchList = this.containsSearch(docElementList, similarityKeyword);
            if (null != matchList && matchList.size() > 0) {
                allMatchList.addAll(matchList);
            }
        }

        //计算DocElement匹配关键词数量
        Map<Integer, Integer> countMap = new HashMap<>();
        Map<Integer, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < allMatchList.size(); i++) {
            DocElement docElement = allMatchList.get(i);
            if (countMap.containsKey(docElement.getIndex())) {
                Integer count = countMap.get(docElement.getIndex());
                countMap.put(docElement.getIndex(), count + 1);
            } else {
                //记录匹配次数
                countMap.put(docElement.getIndex(), 1);
                //记录docElement索引
                indexMap.put(docElement.getIndex(), i);
            }
        }
        //获取匹配次数最多的docElement
        Integer index = null;
        int maxCount = 0;
        for (Integer key : countMap.keySet()) {
            int count = countMap.get(key);
            if (count > maxCount) {
                maxCount = count;
                index = key;
            } else if (count == maxCount) {
                //若匹配数量相同则取索引最小的（即显示在前面的）
                if (key < index) {
                    index = key;
                }
            }
        }
        if (null == index) {
            return null;
        }
        Integer matchIndex = indexMap.get(index);
        DocElement docElement = allMatchList.get(matchIndex);
        return docElement;
    }

    private List<DocElement> containsSearch(List<DocElement> docElementList, SimilarityKeyword similarityKeyword) {
        List<DocElement> matchList = new ArrayList<>();
        for (DocElement docElement : docElementList) {
            SimilarInfo similarInfo = StringUtil.getSimilarInfo(docElement.getValue(), similarityKeyword.getKeyword(), similarityKeyword.getScore(), 2);
            if (null != similarInfo) {
                if (similarInfo.getIsSimilar()) {
                    if (null != similarInfo.getRealScore()) {
                        docElement.setScore((float) similarInfo.getRealScore());
                    }
                    matchList.add(docElement);
                }
            }
        }
        return matchList;
    }

    /**
     * 获取子列表 合并相同行。比如获取“信用卡”下的“发生过逾期的贷记卡账户明细”
     *
     * @param docElementList 1个显示块的字符串列表。比如“信用卡”下的所有子列表集合
     * @param title          标题关键词，全词匹配模式。比如：“发生过逾期的贷记卡账户明细”
     * @param xdocFillter    过滤器，自定义实现是否已结束、是否是换行数据
     * @return 列表。比如：“发生过逾期的贷记卡账户明细”下面的列表
     */
    public List<String> getDocListByTitle(List<DocElement> docElementList, String title, XdocParagraphFillter xdocFillter) {
        if (null == docElementList) {
            return null;
        }
        List<String> result = null;

        Integer startIndex = null;
        for (int i = 0; i < docElementList.size(); i++) {
            String itemStr = docElementList.get(i).getValue();
            if (StringUtil.isSimilar(title, itemStr, 8)) {
                startIndex = i;
                break;
            }
        }
        if (startIndex != null && startIndex < docElementList.size() - 1) {
            //截取开头
            List<DocElement> subList = docElementList.subList(startIndex + 1, docElementList.size());

            List<String> itemList = new ArrayList<>();
            //提取相同显示块数据列表
            for (int i = 0; i < subList.size(); i++) {
                String itemStr = subList.get(i).getValue();
                boolean isSameBlock = xdocFillter.isSameBlock(itemStr);
                if (isSameBlock) {
                    itemList.add(itemStr);
                } else {
                    break;
                }
            }

            //合并相同行
            result = this.mergeSameLine(itemList, xdocFillter);
        }

        return result;
    }


//    public String getWordText(XWPFDocument xdoc) {
//        String text = null;
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < xdoc.getParagraphs().size(); i++) {
//            XWPFParagraph paragraph = xdoc.getParagraphArray(i);
//            sb.append(paragraph.getText());
//        }
//        text = sb.toString();
//        return text;
//    }

    public List<String> mergeDocSameLine(List<DocElement> docElementList, XdocParagraphFillter xdocFillter) {
        List<String> strList = new ArrayList<>();
        for (DocElement docElement : docElementList) {
            strList.add(docElement.getValue());
        }
        return this.mergeSameLine(strList, xdocFillter);
    }

    /**
     * 合并相同行
     *
     * @param strList
     * @param xdocFillter
     * @return
     */
    public List<String> mergeSameLine(List<String> strList, XdocParagraphFillter xdocFillter) {
        List<String> itemList = new ArrayList<>();

        for (int i = 0; i < strList.size(); i++) {
            String itemStr = strList.get(i);
            if (i > 0) {
                String currentLine = itemStr;
                String prevLine = itemList.get(itemList.size() - 1);
                boolean isSameLine = xdocFillter.isSameLine(prevLine, currentLine);
                if (isSameLine) {
                    itemList.set(itemList.size() - 1, prevLine.concat(itemStr));
                } else {
                    itemList.add(itemStr);
                }
            } else {
                itemList.add(itemStr);
            }
        }
        return itemList;
    }

    /**
     * 获取xdoc去除空格的DocElement列表
     *
     * @param xdoc
     * @return
     */
    private List<DocElement> getParagraphUnSpaceList(XWPFDocument xdoc) {
        List<DocElement> list = new ArrayList<DocElement>();
        for (int i = 0; i < xdoc.getParagraphs().size(); i++) {
            XWPFParagraph paragraph = xdoc.getParagraphArray(i);
            String item = paragraph.getText();
            if (StringUtil.isEmpty(item)) {
                continue;
            }
            item = StringUtils.deleteWhitespace(item);
            list.add(new DocElement(i, item));
        }
        return list;
    }

    /**
     * 段落范围获取
     * 查找失败则按换行字符串作为查找标准，获取字符串范围
     *
     * @param start 不包含start
     * @param end   不包含end
     * @return if not found return null
     */
    public List<DocElement> getDocElementListBetweenKeywords(XWPFDocument xdoc, String start, String end) {
        return this.getDocElementListBetweenKeywords(xdoc, new SimilarityKeyword(start, 60), new SimilarityKeyword(end, 60));
    }

    private List<DocElement> getDocElementListBetweenKeywords(XWPFDocument xdoc, SimilarityKeyword start, SimilarityKeyword end) {
        List<DocElement> list = this.getParagraphUnSpaceList(xdoc);
        DocRange docRange = this.getDocRange(list, start, end);
        if (null == docRange
                || null == docRange.getStartIndex() || null == docRange.getEndIndex()) {
            return null;
//            list = this.getDocElementList(xdoc);
//            docRange = this.getDocRange(list, start, end);
//            if (null == docRange
//                    || null == docRange.getStartIndex() || null == docRange.getEndIndex()) {
//                return null;
//            }
        }

        //subList(包含的开始index，不包含的结束index)
        return list.subList(docRange.getStartIndex() + 1, docRange.getEndIndex());
    }

    /**
     * 获取run list
     * 注意：
     * · 跳过empty的字符串
     * · 根据换行再次分割
     *
     * @param paragraph
     * @return
     */
    public List<DocElement> getRunList(XWPFParagraph paragraph) {
        List<DocElement> list = new ArrayList<DocElement>();
        for (int i = 0; i < paragraph.getRuns().size(); i++) {
            String item = paragraph.getRuns().get(i).getPictureText();
            if (StringUtil.isEmpty(item)) {
                continue;
            }
            item = StringUtils.replaceAll(item, "：", ":");
            //换行的再次分割存储、冒号再次分割存储，提升相似匹配度
            String[] subItems = StringUtils.split(item, System.lineSeparator());
            if (subItems.length > 1) {
                for (String subItem : subItems) {
                    if (StringUtil.isEmpty(subItem)) {
                        continue;
                    }
                    String[] subItemStrs = StringUtils.split(subItem, ":");
                    for (String itemSub : subItemStrs) {
                        list.add(new DocElement(i, itemSub));
                    }
                }
            } else {
                list.add(new DocElement(i, item));
                String[] subItemStrs = StringUtils.split(item, ":");
                for (String itemSub : subItemStrs) {
                    list.add(new DocElement(i, itemSub));
                }
            }
        }
        return list;
    }

    /**
     * 获取表格单元格值列表
     *
     * @param xdoc
     * @param referenceList
     * @param startKeyword  关键词限定表格范围，在此关键词之后的表格才会进行匹配   不包含start
     * @param endKeyword    关键词限定表格范围，在此关键词之后的表格才会进行匹配   不包含end
     * @return
     */
    public List<DocElement> getTableCellList(XWPFDocument xdoc, List<DocTableCell> referenceList, SimilarityKeyword startKeyword, SimilarityKeyword endKeyword) {
        List<DocElement> docElementList = this.getBodyElementList(xdoc);

        DocRange docRange = this.getDocRange(docElementList, startKeyword, endKeyword);
        if (null == docRange) {
            return null;
        }
        if (null == docRange.getStartIndex() || null == docRange.getEndIndex()) {
            return null;
        }

        //范围循环取得表格
        List<DocElement> list = new ArrayList<>();
        for (int index = 0; index < xdoc.getBodyElements().size(); index++) {
            IBodyElement iBodyElement = xdoc.getBodyElements().get(index);
            if (iBodyElement instanceof XWPFParagraph) {
                //段落
            } else if (iBodyElement instanceof XWPFTable) {
                //表格
                XWPFTable table = (XWPFTable) iBodyElement;
                if (StringUtil.isEmpty(table.getText())) {
                    continue;
                }
                List<XWPFTableRow> rows = table.getRows();
                for (int i = 0; i < rows.size(); i++) {
                    for (DocTableCell includeCell : referenceList) {
                        if (i == includeCell.getRowIndex()) {
                            XWPFTableRow row = rows.get(i);
                            List<XWPFTableCell> cells = row.getTableCells();
                            for (int j = 0; j < cells.size(); j++) {
                                if (j == includeCell.getColumnIndex()) {
                                    XWPFTableCell cell = cells.get(j);
                                    String cellValue = cell.getText();
                                    DocElement docElement = new DocElement(index, cellValue);
                                    list.add(docElement);
                                }
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 根据关键词获取范围索引
     *
     * @param docElementList
     * @param startKeyword   为null则start索引是0
     * @param endKeyword     为null则end索引是docElementList.size()
     * @return 匹配失败index是null
     */
    private DocRange getDocRange(List<DocElement> docElementList, SimilarityKeyword startKeyword, SimilarityKeyword endKeyword) {
        if (null == startKeyword && null == endKeyword) {
            return null;
        }
        DocRange docRange = new DocRange();

        if (startKeyword == null) {
            docRange.setStartIndex(0);
        } else {
            for (int i = 0; i < docElementList.size(); i++) {
                DocElement docElement = docElementList.get(i);
                if (XdocUtil.fuzzyContains(docElement.getValue(), startKeyword)) {
                    docRange.setStartIndex(i);
                    break;
                }
            }
        }
        if (null == docRange.getStartIndex()) {
            log.warn("startIndex is null. startKeyword=".concat(startKeyword.getKeyword()));
            return null;
        }

        if (endKeyword == null) {
            docRange.setEndIndex(docElementList.size());
        } else {
            for (int i = docRange.getStartIndex() + 1; i < docElementList.size(); i++) {
                DocElement docElement = docElementList.get(i);
                if (XdocUtil.fuzzyContains(docElement.getValue(), endKeyword)) {
                    docRange.setEndIndex(i);
                    break;
                }
            }
        }
        if (null == docRange.getEndIndex()) {
            log.warn("endIndex is null. endKeyword=".concat(endKeyword.getKeyword()));
            return null;
        }

        return docRange;
    }

    /**
     * word所有元素进行索引，返回段落信息
     *
     * @param xdoc
     * @return
     */
    private List<DocElement> getBodyElementList(XWPFDocument xdoc) {
        List<DocElement> list = new ArrayList<DocElement>();
        for (int i = 0; i < xdoc.getBodyElements().size(); i++) {
            IBodyElement iBodyElement = xdoc.getBodyElements().get(i);
            if (iBodyElement instanceof XWPFParagraph) {
                XWPFParagraph paragraph = (XWPFParagraph) iBodyElement;
                String item = paragraph.getText();
                if (StringUtil.isEmpty(item)) {
                    continue;
                }
                item = StringUtils.deleteWhitespace(item);
                list.add(new DocElement(i, item));
            } else if (iBodyElement instanceof XWPFTable) {
                XWPFTable table = (XWPFTable) iBodyElement;
            }
        }
        return list;
    }

//    /**
//     * @param xdoc
//     * @param docTableCellList      单元格参考关键词
//     * @param similarityKeywordList 应包含的关键词
//     * @param tableStartKeyword     关键词限定表格范围，在此关键词之后的表格才会进行匹配   不包含start
//     * @param tableEndKeyword       关键词限定表格范围，在此关键词之后的表格才会进行匹配   不包含end
//     * @return
//     */
//    public XWPFTable getTable(XWPFDocument xdoc, List<DocTableCell> docTableCellList, List<SimilarityKeyword> similarityKeywordList, SimilarityKeyword tableStartKeyword, SimilarityKeyword tableEndKeyword) {
//        List<DocElement> docElementList = this.getTableCellList(xdoc, docTableCellList, tableStartKeyword, tableEndKeyword);
//
//        DocElement docElement = this.containsSearch(docElementList, similarityKeywordList);
//        if (null == docElement || docElement.getIndex() == null) {
//            return null;
//        }
//
//        IBodyElement iBodyElement = xdoc.getBodyElements().get(docElement.getIndex());
//        if (!(iBodyElement instanceof XWPFTable)) {
//            return null;
//        }
//        XWPFTable table = (XWPFTable) iBodyElement;
//        return table;
//    }

    /**
     * 取得下一个表格元素
     *
     * @param xdoc
     * @param bodyElementIndex
     * @return
     */
    public DocTable getNextTable(XWPFDocument xdoc, int bodyElementIndex) {
        XWPFTable nextTable = null;
        //最多允许3个元素是下一个表格
        int nextTableMax = 5;
        int nextTableNo = 1;
        Integer nextIndex = null;
        while (null == nextTable && nextTableNo <= nextTableMax) {
            nextTableNo++;
            nextIndex = bodyElementIndex + nextTableNo;
            if (nextIndex < xdoc.getBodyElements().size()) {
                IBodyElement nextBodyElement = xdoc.getBodyElements().get(nextIndex);
                if (nextBodyElement instanceof XWPFTable) {
                    nextTable = (XWPFTable) nextBodyElement;
                    break;
                }
            }
        }
        if (null == nextTable) {
            return null;
        }
        return new DocTable(nextIndex, nextTable);
    }

    /**
     * 获取单元格值
     *
     * @param table
     * @param docTableCell
     * @return
     */
    public String getTableCellStr(XWPFTable table, DocTableCell docTableCell) {
        String cellStr = null;
        //获取下一个表格
        if (table.getRows().size() > docTableCell.getRowIndex()) {
            //获取第一行字符串
            XWPFTableRow row = table.getRow(docTableCell.getRowIndex());
            if (row.getTableCells().size() > docTableCell.getColumnIndex()) {
                XWPFTableCell cell = row.getCell(docTableCell.getColumnIndex());
                cellStr = cell.getText();
            }
        }
        return cellStr;
    }
}
