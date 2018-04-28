package com.yingu.project.service.service.simple.field;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 报告时间
 *
 * @Date: Created in 2018/3/30 17:33
 * @Author: wm
 */
@Slf4j
public class BaseInfoGenerationDateTime extends BaseField<Date> {
    @Override
    public String[] getSimiStart() {
        return new String[]{
                "报告时间:", "告时间:", "时间:", "间:"
                , "报告时间", "告时间", "时间", "间"
        };
    }

    @Override
    public String[] getSimiEnd() {
        return new String[]{
            "证件号码:","证件号码","证件号","证件","证"
                ,"姓名","姓"
        };
    }

    @Override
    public boolean verifyValueStr(String valueStr) {
        return true;
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    @Override
    public String revise(String valueStr) {
        SimpleDateFormat simpleDateFormatTemp = new SimpleDateFormat("yyyy.MM.ddHH:mm:ss");
        try {
            Date date = simpleDateFormatTemp.parse(valueStr);
            return simpleDateFormat.format(date);
        } catch (Exception e){
            log.error("revise error simpleDateFormat str="+valueStr);
        }
        return valueStr;
    }

    @Override
    public Date convert() {
        try {
            return this.simpleDateFormat.parse(this.getValueStr());
        } catch (Exception e) {
            log.error("convert error str="+this.getValueStr());
        }
        return null;
    }
}
