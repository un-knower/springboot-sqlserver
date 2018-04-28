package com.yingu.project.util.utils;

import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;

public class JPara extends XWPFParagraph implements IJBody {
    public JPara(CTP prgrph, IBody part) {
        super(prgrph, part);
    }

    public JPara(CTP prgrph, IBody part, int index) {
        super(prgrph, part);
        this.index = index;
    }

    int index;

    @Override
    public int getIndex() {
        return index;
    }
}
