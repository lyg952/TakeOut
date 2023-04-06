package com.sasu.takeout.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/*
* 全局异常捕获器，@ControllerAdvice(annotations = {RestController.class,ControllerAdvice.class})
* 设置需要捕获的类，捕获注解为RestController，Controller的类
* */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> ExceptionHandler(SQLIntegrityConstraintViolationException exception){
        if (exception.getMessage().contains("Duplicate entry")){
            String[] split = exception.getMessage().split(" ");
            String msg = split[2]+": " + "已经存在";
            return Result.error(msg);
        }
        return Result.error("未知错误，失败");
    }

    @ExceptionHandler(CustomException.class)
    public Result<String> ExceptionHandler(CustomException exception){
        return Result.error(exception.getMessage());
    }
}
