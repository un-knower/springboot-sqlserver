package com.yingu.project.rest.controller;

import com.alibaba.fastjson.JSON;
import com.yingu.project.ApplicationTest;
import com.yingu.project.api.PersonCreditParam;
import com.yingu.project.api.ResponseOneResult;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.*;
import com.yingu.project.service.service.impl.personcreditprocessor.CreditCardProcessor;
import com.yingu.project.service.service.impl.personcreditprocessor.InquiryRecordProcessor;
import com.yingu.project.service.service.impl.personcreditprocessor.LoanInfoProcessor;
import com.yingu.project.service.service.impl.personcreditprocessor.SemiCreditCardProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTest.class)
public class AntifraudControllerTest {
    @Autowired
    AntifraudController antifraudController;
    @Autowired
    LoanInfoProcessor loanInfoProcessor;
    @Autowired
    CreditCardProcessor creditCardProcessor;
    @Autowired
    SemiCreditCardProcessor semiCreditCardProcessor;
    @Autowired
    InquiryRecordProcessor inquiryRecordProcessor;

    PersonCreditParam personCreditParam = new PersonCreditParam();
    PersonCredit personCredit = new PersonCredit();
    String wordName = "001.docx";

    @PostConstruct
    public void init() {
//        wordName = "2012.docx";
//        wordName = "2011-2.docx";
//        wordName = "2011-4.docx";

        personCreditParam.setXdoc(xdoc());
    }

    @Test
    public void analyseCreditWord() {
        try {
            ResponseOneResult<PersonCredit> result = antifraudController.analyseCreditWord(wordName);
            log.info("\n\nanalyseCreditWord\n" + JSON.toJSONString(result));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void LoanInfo() {
        XWPFDocument xdoc = xdoc();
        List<LoanInfo> loanInfoList = loanInfoProcessor.getInfo(xdoc);
        log.info("\n\n" + JSON.toJSONString(loanInfoList));
    }

    @Test
    public void CreditCard() {
        creditCardProcessor.analyse(personCreditParam, personCredit);
        log.info("\n\n" + JSON.toJSONString(personCredit.getCreditCardList()));
    }

    @Test
    public void SemiCreditCard() {
        semiCreditCardProcessor.analyse(personCreditParam, personCredit);
        log.info("\n\n" + JSON.toJSONString(personCredit.getSemiCreditCardList()));
    }

    @Test
    public void InquiryRecord() {
        XWPFDocument xdoc = xdoc();
        List<InquiryRecord> list = inquiryRecordProcessor.getInfo(xdoc);
        log.info("\n\n" + JSON.toJSONString(list));
    }

    //xdoc
    private XWPFDocument xdoc() {
        File file = new File("D:\\word\\" + wordName);
        XWPFDocument xdoc = new XWPFDocument();
        try {
            FileInputStream fis = new FileInputStream(file);
            xdoc = new XWPFDocument(fis);
            fis.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
//        if (null == xdoc)
        return xdoc;
    }
}
