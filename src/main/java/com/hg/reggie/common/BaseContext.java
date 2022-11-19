package com.hg.reggie.common;

/**
 * 用户保存和获取当前登录用户的id
 * @author hougen
 * @program Reggie
 * @description thread Local 为每个线程提供一个单独的存储空间,具有线程隔离效果,只有在线程内才能获取对应的值,线程外则不能
 * @create 2022-11-14 01:24
 */

public class BaseContext {
    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        THREAD_LOCAL.set(id);
    }

    public static Long getCurrentId(){
        return THREAD_LOCAL.get();
    }

    public static void remove(){
        THREAD_LOCAL.remove();
    }
}


