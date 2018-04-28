package com.yingu.project.service.service.simple.impl;

import com.yingu.project.service.service.simple.ExtractInfoService;
import com.yingu.project.service.service.simple.PDFParseService;
import lombok.SneakyThrows;
import lombok.experimental.var;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Date: Created in 2018/3/26 10:19
 * @Author: wm
 */
@Service
public class PDFParseServiceImpl implements PDFParseService {
    private static ThreadLocal<PDFTextStripper> threadLocalPdfStripper = new ThreadLocal<PDFTextStripper>();
    @Autowired
    private ExtractInfoService extractInfoService;

    @Override
    @SneakyThrows(IOException.class)
    public Map analyse(byte[] bytes) {
        if (null == bytes) {
            throw new RuntimeException("bytes is null");
        }
        PDFTextStripper pdfStripper = new PDFTextStripper();
        threadLocalPdfStripper.set(pdfStripper);
        var resultMap = new LinkedHashMap<String, Object>();
        var document = PDDocument.load(bytes);
        var text = threadLocalPdfStripper.get().getText(document);
        document.close();

        return this.analyse(text);
    }

    public Map analyse(String text){
        var resultMap = new LinkedHashMap<String, Object>();
//        if (null==extractInfoService){
//            extractInfoService = new ExtractInfoSimilarityServiceImpl();
//        }
        extractInfoService.getBaseInfo(text, resultMap);  //报告头
        extractInfoService.getCreditInfo(text, resultMap);  //信息概要
        extractInfoService.getOverdueInfo(text, resultMap);  //信贷记录
        extractInfoService.getInquiryInfo(text, resultMap);  //查询记录
        extractInfoService.getWarrantInfo(text, resultMap); // 担保信息
        return resultMap;
    }

}
