package com.yingu.project.service.service.impl.personcreditprocessor;

import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.PersonCreditProcessorType;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.CreditBase;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.ResidenInfo;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.SpouseInfo;
import com.yingu.project.service.service.PersonCreditProcessor;
import com.yingu.project.util.utils.DateUtil;
import com.yingu.project.util.utils.StringUtil;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MMM on 2018/03/23.
 * 居住信息处理器
 */
@Component
public class ResidenInfoProcessor extends PersonCreditProcessor {
    public List<ResidenInfo> getCreditInfoList(XWPFTable table) {
        List<ResidenInfo> residenInfoList=new ArrayList<>();
        ResidenInfo residenInfo=null;
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        XWPFTableRow row;
        XWPFTableCell cell;
        String cellValue;
        rows = table.getRows();
        int i=0;
        Boolean isBreak=false;
        for(;i<rows.size();i++){
            row=rows.get(i);
            cells = row.getTableCells();
            for(int j=0;j<cells.size();j++){
                cell=cells.get(j);
                String cellKey= StringUtil.trim(cell.getText());
                if(cellKey.equals("编号")){
                    i++;
                    isBreak=true;
                    break;
                }
            }
            if(isBreak){
                break;
            }
        }
        if(isBreak) {
            for (; i < rows.size(); i++) {
                row = rows.get(i);
                cells = row.getTableCells();
                residenInfo = new ResidenInfo();
                for (int j = 0; j < cells.size(); j++) {

                    cell = cells.get(j);
                    cellValue = StringUtil.trim(cell.getText());
                    if (j == 0) {
                        residenInfo.setResidenNo(cellValue);
                    } else if (j == 1) {
                        residenInfo.setAddress(cellValue);
                    } else if (j == 2) {
                        residenInfo.setLivingState(cellValue);
                    } else if (j == 3) {
                        //residenInfo.setUpdateTime(DateUtil.StrToDate2(StringUtil.trimToDate(cellValue)));
                        Date date= DateUtil.StrToDate2(StringUtil.trimToDate(cellValue));
                        residenInfo.setUpdateTime(date);
                        residenInfo.setStrUpdateTime(cellValue);
                    } else if (j == 4) {
                        residenInfo.setDataGenerating(cellValue);
                    }

                }
                residenInfoList.add(residenInfo);
            }
        }

        return residenInfoList;
    }

    public void setCreditEntityList(PersonCredit personCredit, List<ResidenInfo> creditBaseList) {
        personCredit.setResidenInfoList(creditBaseList);
    }

    @Override
    public Boolean analyse(PersonCreditParam personCreditParam, PersonCredit personCredit) {
        List<ResidenInfo> residenInfoList = getCreditInfoList(personCreditParam.getTable());
        if (residenInfoList!= null) {
            setCreditEntityList(personCredit,residenInfoList);
            return true;
        } else {
            return false;
        }
    }
    @Override
    public String getProcessorType() {
        return PersonCreditProcessorType.ResidenInfoProcessor.getValue();
    }
}
