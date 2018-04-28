package com.yingu.project.util.utils;

import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;

public class JTable extends XWPFTable implements IJBody {
    public JTable(CTTbl table, IBody part) {
        super(table, part);
    }

    public JTable(CTTbl table, IBody part,int index) {
        super(table, part);
        this.index = index;
    }
    int index;

    @Override
    public int getIndex() {
        return index;
    }

    int indexTable;
}
