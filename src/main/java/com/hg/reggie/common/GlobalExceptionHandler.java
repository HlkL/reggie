package com.hg.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * ControllerAdvice 结合方法型注解@ExceptionHandler，用于捕获Controller中抛出的指定类型的异常
 *
 * @author hougen
 * @program Reggie
 * @description 全局异常处理
 * @create 2022-11-13 17:10
 */
@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     *  员工添加异常处理
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        if( ex.getMessage().contains("Duplicate entry") ){
            String[] split = ex.getMessage().split(" ");
            return R.error(split[2]+"已存在");
        }
        return R.error("错误");
    }

    /**
     *  分类删除异常处理
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}


