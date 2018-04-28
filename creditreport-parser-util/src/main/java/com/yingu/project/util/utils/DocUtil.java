package com.yingu.project.util.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DocUtil {

    /**
     * 获取某项信息的全部表格
     * 一、因为分页，将一个表格截断成多个表格
     *
     * @param itemBegin
     * @param itemEnd
     * @param xdoc
     * @return
     */
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

    public static List<XWPFTable> getTablesItem(String itemBegin, List<String> itemEnd, XWPFDocument xdoc) {
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
                if (flag && itemBegin.equals(paraText)) {
                    indexBegin = i;
                    flag = false;
                } else if (itemEnd.contains(paraText)) {
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
            if (TableUtil.determineType(table, itemBegin)) {
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

    public static List<XWPFTable> getTablesItem(List<String> itemBegin, List<String> itemEnd, XWPFDocument xdoc) {
        List<IBodyElement> elements = xdoc.getBodyElements();
        //找起止index
        int indexBegin = -1;
        int indexEnd = -1;
        boolean flag = true;
        for (int i = 0; i < elements.size(); i++) {
            IBodyElement element = elements.get(i);
            if (element instanceof XWPFParagraph) {
                XWPFParagraph para = (XWPFParagraph) element;
                String paraText = TableUtil.text(para);
                if (flag && keywordMatching(itemBegin, paraText)) {
                    indexBegin = i;
                    flag = false;
                } else if (keywordMatching(itemEnd, paraText)) {
                    indexEnd = i;
                    break;
                }
            } else if (element instanceof XWPFTable) {

            }
        }
        if (indexBegin == -1 || indexEnd == -1)
            return null;
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

    public static boolean keywordMatching(List<String> keywordList, String paraText) {
        for (int i = 0; i < keywordList.size(); i++) {
            String keyword = keywordList.get(i);
            if (StringUtil.isSimilarCountEqual(keyword, paraText)) {
                return true;
            }
        }
        return false;
    }

    public static DocIndex getIndex(XWPFDocument xdoc, List<String> itemBegin, List<String> itemEnd) {
        List<IBodyElement> elements = xdoc.getBodyElements();
        int indexBegin = indexBegin(itemBegin, elements);
        int indexEnd = indexEnd(indexBegin, itemEnd, elements);
//        if (indexBegin == -1 || indexEnd == -1)
//            return null;
        return new DocIndex(indexBegin, indexEnd);
    }

    private static int indexBegin(List<String> itemBegin, List<IBodyElement> elements) {
        int indexBegin = -1;
        for (int i = 0; i < elements.size(); i++) {
            IBodyElement element = elements.get(i);
            if (element instanceof XWPFParagraph) {
                XWPFParagraph para = (XWPFParagraph) element;
                String paraText = TableUtil.textSubhead(para);
                if (keywordMatching(itemBegin, paraText)) {
                    indexBegin = i > indexBegin ? i : indexBegin;
                }
            }
        }
        return indexBegin;
    }

    private static int indexEnd(int indexBegin, List<String> itemEnd, List<IBodyElement> elements) {
        int indexEnd = -1;
        for (int i = indexBegin + 1; i < elements.size(); i++) {
            IBodyElement element = elements.get(i);
            if (element instanceof XWPFParagraph) {
                XWPFParagraph para = (XWPFParagraph) element;
                String paraText = TableUtil.textSubhead(para);
                if (keywordMatching(itemEnd, paraText)) {
                    return i;
//                    indexEnd = i < indexEnd ? i : indexEnd;
                }
            }
        }
        return indexEnd;
    }

    //根据小标题取某个业务的全部elements，且删除空行
    public static List<IBodyElement> getElements(XWPFDocument xdoc, List<String> KeywordBegin, List<String> KeywordEnd) {
        DocIndex index = getIndex(xdoc, KeywordBegin, KeywordEnd);
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
        return elements;
    }

    public static List<IBodyElement> getElements(XWPFDocument xdoc, DocIndex index) {
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
        return elements;
    }

    public static List<XWPFTable> getTablesItem(XWPFDocument xdoc, DocIndex index) {
        List<IBodyElement> elements = xdoc.getBodyElements();
        List<XWPFTable> tables = new ArrayList<>();
        for (int i = index.getBegin() + 1; i < index.getEnd(); i++) {
            IBodyElement element = elements.get(i);
            if (element instanceof XWPFTable) {
                tables.add((XWPFTable) element);
            }
        }
        return tables;
    }

    public static List<XWPFTable> getTables(List<IBodyElement> elements) {
        List<XWPFTable> paras = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            IBodyElement element = elements.get(i);
            if (element instanceof XWPFTable) {
                paras.add((XWPFTable) element);
            }
        }

        return paras;
    }

    public static boolean listEmpty(List list) {
        if (list == null || list.size() == 0)
            return true;
        return false;
    }

    //取全部段落
    public static List<XWPFParagraph> getParas(List<IBodyElement> elements) {
        List<XWPFParagraph> paras = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            IBodyElement element = elements.get(i);
            if (element instanceof XWPFParagraph) {
                paras.add((XWPFParagraph) element);
            }
        }

        return paras;
    }

    //根据para找对应的表格，段落下面的表格
    public static XWPFTable getTable(XWPFParagraph para, List<IBodyElement> elements) {
        int index = elements.indexOf(para);
        if (index + 1 < elements.size()) {
            IBodyElement element = elements.get(index + 1);
            if (element instanceof XWPFTable) {
                return (XWPFTable) element;
            }
        }
        return null;
    }

}
