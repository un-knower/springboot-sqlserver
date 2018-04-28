package com.yingu.project.service.service.simple;

import com.alibaba.fastjson.JSON;
import com.yingu.project.service.service.simple.impl.PDFParseServiceImpl;
import com.yingu.project.util.utils.FileUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Map;

/**
 * @Date: Created in 2018/3/26 10:52
 * @Author: wm
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//@SpringBootApplication
@ComponentScan("com.yingu.project.service.service.simple")
public class PDFParseServiceTest {
    @Autowired
    PDFParseService pdfParseService;

    @Test
    public void analyse() {
        String filePath = "d:/word/wm-normal.pdf";
        File file = new File(filePath);
        byte[] bytes = FileUtil.fileToByte(file);
        Map map = pdfParseService.analyse(bytes);
        System.out.println(JSON.toJSONString(map, true));
    }
}
