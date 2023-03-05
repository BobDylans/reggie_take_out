package com.itcast.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.ldap.Control;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.sql.SQLIntegrityConstraintViolationException;

/*
全局异常处理
如果项目抛异常,则会在这里进行统一处理

 */
@ControllerAdvice(annotations = {RestController.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    //一旦controller抛出这个异常,就会被拦截然后统一进行处理
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> ExceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] s = ex.getMessage().split(" ");
            String meg=s[2]+"已存在";

            return R.error(meg);

        }
        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> ExceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }

}
