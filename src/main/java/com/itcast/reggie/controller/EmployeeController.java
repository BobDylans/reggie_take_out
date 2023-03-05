package com.itcast.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.reggie.common.R;
import com.itcast.reggie.entity.Employee;
import com.itcast.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //登陆操作
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
            //返回值中包含的是emp,是为了让网页暂时保存信息,以便下次登录
        return R.success(emp);
    }

    //退出操作
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
        //具体的跳转页面的功能是在前端代码中实现的
    }

    //新增员工
    @PostMapping
    //由于传送回来的是一个json格式的employee数据,所以我们要使用@RequestBody注解来接收
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工,员工信息: {}",employee.toString());
        //统一给初始员工一个12345 密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录用户的id
        Long employeeId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(employeeId);
        employee.setUpdateUser(employeeId);

        employeeService.save(employee);
        //由于我们只需要在controller中接收用户信息而不需要再向其返回,所以只用一个String即可
        return R.success("添加成功");

    }


    //分页查询,接收页面发送的请求

    @GetMapping("/page")
    //get请求本身就是以键值对的形式来存储数据额
    //可以直接从网页中获取相关的page参数,因为这些信息原本就是以键值对的形式存储在json中,所以可以直接获取
    public R<Page<Employee>> page(String name,int page,int pageSize){
        log.info("name={},page = {},pageSize = {}",name,page,pageSize);
        //构造分页构造器
        Page pageInfo =new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> wrapper=new LambdaQueryWrapper<>();
        //如果没有向get请求中添加name属性的值的话,就不会按照name来进行查询
            //添加按名查询条件
            wrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
            //添加排序查询
            wrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,wrapper);

        return R.success(pageInfo);
    }
    //根据id来修改员工信息
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        Long employeeId = (Long) request.getSession().getAttribute("employee");
//        //记录修改时间
//        employee.setUpdateTime(LocalDateTime.now());
//        //通过req来获取修改人的信息
//        employee.setUpdateUser(employeeId);

        //嗲用Service\中的方法来进行修改
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }
    //用来回显数据
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id){
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
      return R.error("没有查询到相应员工信息");

    }
}
