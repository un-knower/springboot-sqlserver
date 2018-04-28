package com.yingu.project.rest.config;

import com.yingu.project.api.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by MMM on 2018/02/28.
 */
/**
 * Created by sunyuming on 17/5/11.
 * 全局异常日志捕获,并写日志
 */
@ControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public void defaultErrorHandler(HttpServletRequest req, Exception e)  {

        log.error(e.getMessage(),e);
    }

}