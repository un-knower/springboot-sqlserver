package com.yingu.project.service.service.simple;

import com.yingu.project.persistence.mongo.entity.abridged.BaseInfo;
import com.yingu.project.persistence.mongo.entity.abridged.CreditDetail;
import com.yingu.project.persistence.mongo.entity.abridged.InquiryInfo;
import com.yingu.project.persistence.mongo.entity.abridged.PublicRecord;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * 简版征信报告 相似匹配
 * @Date: Created in 2018/3/26 10:36
 * @Author: wm
 */
public interface ExtractInfoSimilarityService {
    BaseInfo getBaseInfo(XWPFDocument xdoc);

    InquiryInfo getInquiryInfo(XWPFDocument xdoc);

    CreditDetail getCreditDetail(XWPFDocument xdoc);

    PublicRecord getPublicRecord(XWPFDocument xdoc);
}
