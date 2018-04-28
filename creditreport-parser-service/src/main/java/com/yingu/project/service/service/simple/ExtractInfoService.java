package com.yingu.project.service.service.simple;

import java.util.Map;

/**
 * 简版解析器
 * @Date: Created in 2018/3/26 10:36
 * @Author: wm
 */
public interface ExtractInfoService {
    void getBaseInfo(String text, Map<String, Object> resultMap);  /*获取头部的个人信息*/

    void getCreditInfo(String text, Map<String, Object> resultMap);  /*获取信贷记录*/

    void getInquiryInfo(String text, Map<String, Object> resultMap);  /*获取查询记录*/

    void getOverdueInfo(String text, Map<String, Object> resultMap);  /*获取信用卡记录*/

    void getWarrantInfo(String text, Map<String, Object> resultMap); /*获取担保记录*/
}
