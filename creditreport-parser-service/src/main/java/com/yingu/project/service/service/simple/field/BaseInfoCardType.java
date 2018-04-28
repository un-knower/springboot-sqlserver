package com.yingu.project.service.service.simple.field;

/**
 * 证件类型
 * @Date: Created in 2018/3/30 17:19
 * @Author: wm
 */
public class BaseInfoCardType extends BaseField<String>{
    @Override
    public String[] getSimiStart() {
        return new String[]{
                "证件类型:","件类型:","类型:","型:"
                ,"证件类型","件类型","类型","型"
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
