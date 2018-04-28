package com.yingu.project.service.service.simple.field;

/**
 * 报告编号
 * @Date: Created in 2018/3/30 9:28
 * @Author: wm
 */
public class BaseInfoContractNo extends BaseField<String> {
    @Override
    public String[] getSimiStart() {
        return new String[]{
                "报告编号:", "告编号:", "编号:"
                , "报告编号", "告编号", "编号"
        };
    }

    @Override
    public String[] getSimiEnd() {
        return new String[]{
                "姓名:","姓名","姓"
                ,"名:","名"
                ,"查询时间","查询时","查询","查"
        };
    }

    @Override
    public boolean verifyValueStr(String valueStr) {
        return true;
    }

    @Override
    public String revise(String valueStr) {
        return null;
    }

    @Override
    public String convert() {
        return null;
    }
}
