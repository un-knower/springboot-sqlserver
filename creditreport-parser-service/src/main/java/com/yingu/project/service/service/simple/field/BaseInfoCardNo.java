package com.yingu.project.service.service.simple.field;

import org.apache.commons.lang3.StringUtils;

/**
 * 证件号码
 * @Date: Created in 2018/3/30 17:52
 * @Author: wm
 */
public class BaseInfoCardNo extends BaseField<String> {
    @Override
    public String[] getSimiStart() {
        return new String[]{
                "证件号码:","件号码:","号码:","码:"
                ,"证件号码","件号码","号码","码"
        };
    }

    @Override
    public String[] getSimiEnd() {
        return null;
    }

    @Override
    public boolean verifyValueStr(String valueStr) {
        if (valueStr.length()<4){
            return false;
        }
        return true;
    }

    @Override
    public String revise(String valueStr) {
        return StringUtils.substring(valueStr, 0, valueStr.length()-2);
    }

    @Override
    public String convert() {
        return null;
    }
}
