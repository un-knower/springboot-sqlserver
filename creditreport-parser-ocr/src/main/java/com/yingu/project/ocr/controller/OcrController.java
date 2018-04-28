package com.yingu.project.ocr.controller;

import com.yingu.project.api.AbbyyDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@Slf4j
@RestController
@RequestMapping(value = "ocr")
public class OcrController {
    @Autowired
    AbbyyService abbyyService;

    @PostConstruct
    void initialize() {
        log.info("initialize");
//        abbyyService.initialize();
    }

    /**
     * 200 OK
     * 400 请求参数有误
     * 500 OCR服务器异常
     *
     * @param aImport
     * @return
     */
    @RequestMapping(value = "/process", method = RequestMethod.POST)
    public AbbyyDto process(@RequestBody AbbyyDto aImport) {
        String name = aImport.getName();
        log.info("process.name:" + name);
        AbbyyDto aExport = new AbbyyDto();
        aExport.setName(name);

        byte[] bytesImport = aImport.getBytes();
        if (bytesImport == null || bytesImport.length == 0) {
            log.info("process.请求参数有误." + aImport.getName());
            aExport.setCode(400);
            aExport.setMessage("请求参数有误");
            return aExport;
        }

        try {
            aExport.setBytes(abbyyService.processImage(bytesImport));
        } catch (Exception ex) {
            log.error("process", ex);
            aExport.setCode(500);
            aExport.setMessage("OCR服务器异常");
            return aExport;
        }
//        aExport.setBytes(aImport.getBytes());

        aExport.setCode(200);
        return aExport;
    }
}
