package com.yingu.project.service.service.simple;

import com.yingu.project.persistence.mongo.entity.abridged.AbridgedCredit;
import com.yingu.project.persistence.mongo.entity.abridged.BaseInfo;
import com.yingu.project.persistence.mongo.entity.abridged.CreditDetail;
import com.yingu.project.persistence.mongo.entity.abridged.InquiryInfo;
import com.yingu.project.persistence.mongo.entity.abridged.PublicRecord;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * @Date: Created in 2018/3/29 17:15
 * @Author: wm
 */
public interface WordService {

    /**
     * 入口 简版个人征信报告解析
     * @param xdoc
     * @param wordName
     * @param ossFileName
     * @return
     */
    public AbridgedCredit analysisAbridged(XWPFDocument xdoc, String wordName, String ossFileName);

//    /**
//     * 获取头部基础信息
//     * @return
//     */
//    public BaseInfo getBaseInfo(XWPFDocument xdoc);
//
//    /**
//     * 信贷记录
//     * @param xdoc
//     * @return
//     */
//    public CreditDetail getCreditDetail(XWPFDocument xdoc);
//
//    /**
//     * 查询记录明细
//     * @param xdoc
//     * @return
//     */
//    public InquiryInfo getInquiryInfo(XWPFDocument xdoc);
//
//    /**
//     * 公共记录
//     * @param xdoc
//     * @return
//     */
//    public PublicRecord getPublicRecord(XWPFDocument xdoc);

}
