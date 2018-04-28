package com.yingu.project.service.service.simple;

import com.fasterxml.uuid.impl.UUIDUtil;
import com.yingu.project.ApplicationTest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

/**
 * @Date: Created in 2018/3/26 10:52
 * @Author: wm
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTest.class)
@ComponentScan("com.yingu.project")
@Slf4j
public class WordServiceTest {
    @Autowired
    WordService wordService;

    @Test
    @SneakyThrows
    public void analyse() {
        String wordName = "DDPHYB1642017120900002.docx";
//        String wordName = "DDPHYB1642017121300001.docx";
//        String wordName = "DDPHYB1642017121500001.docx";
//        String wordName = "DDPHYB1642017121500003.docx";
//        String wordName = "DDPHYB1642017122000001.docx";
        String ossFileName = UUID.randomUUID().toString()+".docx";
        String filePath = "d:/word/" + wordName;
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        XWPFDocument xdoc = new XWPFDocument(fis);
        wordService.analysisAbridged(xdoc, wordName, ossFileName);

        fis.close();
    }

}
