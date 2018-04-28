package com.yingu.project.service.service.simple.xdoc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * xdoc的表格单元格
 * @Date: Created in 2018/4/10 17:22
 * @Author: wm
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocTableCell {
    public DocTableCell(Integer rowIndex,Integer columnIndex){
        this.rowIndex=rowIndex;
        this.columnIndex=columnIndex;
    }
    Integer rowIndex;
    Integer columnIndex;
    String value;
}
