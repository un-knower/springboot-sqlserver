package com.yingu.project.service.service.simple.field;

/**
 * 姓名
 * @Date: Created in 2018/3/30 14:30
 * @Author: wm
 */
public class BaseInfoClientName extends BaseField<String> {
    @Override
    public String[] getSimiStart() {
        return new String[]{
                "姓名:", "名:"
                , "姓名", "名"
        };
    }

    @Override
    public String[] getSimiEnd() {
        return null;
    }

    @Override
    public boolean verifyValueStr(String valueStr) {
        if (valueStr.length() < 2) {
            return false;
        }
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
