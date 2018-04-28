package com.yingu.project.service.service.simple.field;

import com.yingu.project.util.utils.DateUtil;
import lombok.SneakyThrows;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 查询时间
 * @Date: Created in 2018/3/30 15:59
 * @Author: wm
 */
@Slf4j
public class BaseInfoInquiryDateTime extends BaseField<Date> {
    @Override
    public String[] getSimiStart() {
        return new String[]{
                "査询时间:","询时间:","时间:","间:"
                ,"査询时间","询时间","时间","间"
        };
    }

    @Override
    public String[] getSimiEnd() {
        return new String[]{
                "证件类型:","证件类型","证件类","证件","证"
                ,"件类型:","件类型","件类","件"
                ,"类型:","类型","类"
                ,"型:","型"
                ,"报告时间","报告时","报告","报"
        };
    }

    @Override
    public boolean verifyValueStr(String valueStr) {
        return true;
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    @Override
    @SneakyThrows
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
