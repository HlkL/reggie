package com.hg.reggie.common;

/**
 * @author hougen
 * @program Reggie
 * @description 自定义业务异常
 * @create 2022-11-14 15:47
 */

public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}


