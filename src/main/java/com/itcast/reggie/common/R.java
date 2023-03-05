package com.itcast.reggie.common;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;
//通用的返回结果,我们与前端交互的结果最后都会转换为这个类型
//这个类是为了前端的妹妹更好与我们进行交互
@Data
public class R<T> {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据
    //静态方法(会在方法调用时自动创建一个对象),如果成功就会自动返回一个code为1的R对象,代表成功
    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }
    //静态方法,如果成功就会自动返回一个code为0的R对象,代表失败
    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
