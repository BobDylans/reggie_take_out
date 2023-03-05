package com.itcast.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
/*
自定义的与数据对象处理器,这个处理会对某些公共字段进行自动地填充
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    //插入操作自动填充
    //MetaObject中存储的是原始的数据
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充:insert");

        metaObject.setValue("createTime",LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
    //修改操作自动填充
    @Override
    //MetaObject中存储的是原始的数据
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充:update");
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
