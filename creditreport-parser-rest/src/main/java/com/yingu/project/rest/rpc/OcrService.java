package com.yingu.project.rest.rpc;

import com.yingu.project.api.AbbyyDto;
import com.yingu.project.util.config.RpcConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = RpcConfig.ocrService)
public interface OcrService {
    @RequestMapping(value = "ocr/process", method = RequestMethod.POST)
    AbbyyDto process(@RequestBody AbbyyDto aImport);
}