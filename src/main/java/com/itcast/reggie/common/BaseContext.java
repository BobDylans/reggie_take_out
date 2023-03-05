package com.itcast.reggie.common;
//基于ThreadLocal封装的工具类,用于保存和获取当前登录童虎的id
//以线程为作用域,每个线程会单独保存自己的信息,不用安心会弄混
//每一次的http请求都会获得一个独立的线程,而ThreadLocal则可以获得该线程的中存储信息的一个副本


public class BaseContext {
    //存储用户的id
    private static ThreadLocal<Long> threadLocal=new InheritableThreadLocal<>();
    public static void setThreadLocal(Long id){
        threadLocal.set(id);
        //在当前线程中设置id
    }
    public static Long getCurrentId(){
        return threadLocal.get();
        //在当前线程中获取id
    }
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }


}
