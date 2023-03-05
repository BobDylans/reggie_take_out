package com.itcast.reggie.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcast.reggie.entity.Employee;
import com.itcast.reggie.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Test1 {

    @Autowired
    private EmployeeService employeeService;
    @Test
    public  void test1(){
        LambdaQueryWrapper<Employee> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername,"admin");
        Employee one = employeeService.getOne(wrapper);
        System.out.println(one.getId());
    }



    @Test
    void testGetById(){
        System.out.println(employeeService.getById(1));
    }
}
