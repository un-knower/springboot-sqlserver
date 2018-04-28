package com.yingu.project.service.service;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.CreditBase;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.IdentityInfo;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.util.utils.StringUtil;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by MMM on 2018/03/21.
 * 个人信用处理器抽象类
 */
@Component
public abstract class PersonCreditProcessor{
    public abstract String getProcessorType();
    public abstract Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit);
    /**
     * 是否有下一行同列cell
     * @param rows
     * @param i
     * @param j
     * @return
     */
    protected Boolean hasValueCell(List<XWPFTableRow> rows,int i ,int j){
        if(i+1< rows.size()&&j<rows.get(i+1).getTableCells().size()) {
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * 获取下一行同列单元格值
     * @param rows
     * @param i
     * @param j
     * @return
     */
    protected String getCellValue(List<XWPFTableRow> rows,int i ,int j){
        String cellValue= StringUtil.trim(rows.get(i+1).getCell(j).getText());
        return cellValue;
    }

}
