package com.itcast.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.reggie.Dto.DishDto;
import com.itcast.reggie.Dto.SetmealDto;
import com.itcast.reggie.common.R;
import com.itcast.reggie.entity.Category;
import com.itcast.reggie.entity.Setmeal;
import com.itcast.reggie.service.CategoryService;
import com.itcast.reggie.service.SetmealDishService;
import com.itcast.reggie.service.SetmealService;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
套餐管理

 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {


    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    /*
    新增套餐
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息是"+setmealDto.toString() );
        setmealService.saveWithDish(setmealDto);
        return R.success("添加菜品成功");
    }


    @GetMapping("/page")
    //套餐分页查询
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>();

        LambdaQueryWrapper<Setmeal> wrapper=new LambdaQueryWrapper<>();
        //添加查询调价
        wrapper.like(name!=null,Setmeal::getName,name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);

        //这个方法不能删,因为这一步相当于在为pageInfo赋值,这样我们才能为下面的复制操作铺路
        setmealService.page(pageInfo,wrapper);

        //属性拷贝(原,目标)
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list=records.stream().map((item)->{
            //构造DTO
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            //分类Id
            Long categoryId = item.getCategoryId();
            //根据分类Id来查询分类
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;

        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }/*
    删除套餐
    */
    @DeleteMapping
    //可以直接获得携带的参数
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("将要执行删除操作");
        setmealService.removeWithDish(ids);


        return R.success("套餐删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status,Long ids){
        //创建以一个条件构造器
        LambdaQueryWrapper<Setmeal> wrapper=new LambdaQueryWrapper<Setmeal>();
        Setmeal setmeal = setmealService.getById(ids);
        if (status==0){
            setmeal.setStatus(1);
            return R.success("停售成功");
        }else setmeal.setStatus(1);
        setmealService.updateById(setmeal);

        log.info("要进行修改咯");
      return R.success("启售成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        wrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(wrapper);

        return R.success(list);


    }
}
