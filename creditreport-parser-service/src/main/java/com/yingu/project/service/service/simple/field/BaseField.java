package com.yingu.project.service.service.simple.field;

import com.yingu.project.util.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @Date: Created in 2018/3/30 9:23
 * @Author: wm
 */
@Getter
@Setter
@Slf4j
public abstract class BaseField<T> {
    /**不可空*/
    public abstract String[] getSimiStart();
    /**可空*/
    public abstract String[] getSimiEnd();

    /**提取的结果字符串*/
    private String valueStr;
    /**转换为正确类型后的值*/
    private T value;

    /**
     * 验证转换前的字符串
     * 默认有个null和空字符校验
     * 注意：valueStr是纠正前的值
     * @return
     */
    public abstract boolean verifyValueStr(String valueStr);

    /**
     * valueStr数据格式校正
     * @return
     */
    public abstract String revise(String valueStr);

    /**
     * 类型转换
     * 注意：valueStr是经过revise()之后的值
     * @return
     */
    public abstract T convert();


    /**
     * 解析字段
     * 注意：会自动将字符串去换行和空格，自动替换中文符号“：”为英文
     * @param sourceStr     来源字符串
     * @return
     */
    public BaseField analysisValue(String sourceStr){
        String tempStr = StringUtils.deleteWhitespace(sourceStr);
        tempStr = StringUtils.replaceAll(tempStr, "：", ":");
        BaseField field = this;
        String valueStr = null;
        loopStart:for (String start: field.getSimiStart()){
            if (null==field.getSimiEnd()||field.getSimiEnd().length==0){
                valueStr = StringUtils.substringAfter(tempStr, start);
                if (StringUtil.isEmpty(valueStr)){
                    continue;
                }
                Boolean isValid = field.verifyValueStr(valueStr);
                if (isValid){
                    break;
                }
            }else{
                loopEnd:for (String end: field.getSimiEnd()){
                    valueStr = StringUtils.substringBetween(tempStr, start, end);
                    if (StringUtil.isEmpty(valueStr)){
                        continue;
                    }
                    Boolean isValid = field.verifyValueStr(valueStr);
                    if (isValid){
                        break loopStart;
                    }
                }
            }
        }

        String newValueStr = field.revise(valueStr);
        if (null==newValueStr){
            newValueStr = valueStr;
        }
        field.setValueStr(newValueStr);
        T t = (T) field.convert();
        field.setValue(t);
        return field;
    }
}
