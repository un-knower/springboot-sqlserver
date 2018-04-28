package com.yingu.project.service.service.simple.xdoc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.xwpf.usermodel.XWPFTable;

/**
 * @Date: Created in 2018/4/12 13:45
 * @Author: wm
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocTable {
    int index;
    XWPFTable table;
}
