package com.itcast.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data

public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;//代表身份证号码

    private Integer status;

    //只在insert时填充这个字段的值
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //也会被springboot自动识别为驼峰命名法
    //在insert和update时都会自动填充这个字段的值
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;



    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
