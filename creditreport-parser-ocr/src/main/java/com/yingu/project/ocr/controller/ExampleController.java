package com.yingu.project.ocr.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ExampleController {
    @RequestMapping(value = "/hi", method = RequestMethod.GET)
    public String sayHiFromClientOne(@RequestParam(value = "name") String name) {
        log.info("sayHiFromClientOne.name:" + name);
        return name;
    }
}
