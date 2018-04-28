package com.yingu.project.util.utils;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

@Setter
@Getter
public class BodyElement {
    int indexElement;
    BodyElementType elementType;
    XWPFParagraph para;
    XWPFTable table;
    int indexTable = -1;

    public BodyElement(int indexElement, BodyElementType elementType, XWPFParagraph para, XWPFTable table, int indexTable) {
        this.indexElement = indexElement;
        this.elementType = elementType;
        this.para = para;
        this.table = table;
        this.indexTable = indexTable;
    }

    public BodyElement(int indexElement) {
        this.indexElement = indexElement;
    }
}
