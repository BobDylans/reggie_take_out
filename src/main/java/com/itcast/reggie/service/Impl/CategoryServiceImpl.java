package com.itcast.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.reggie.common.CustomException;
import com.itcast.reggie.entity.Category;
import com.itcast.reggie.entity.Dish;
import com.itcast.reggie.entity.Setmeal;
import com.itcast.reggie.mapper.CategoryMapper;
import com.itcast.reggie.service.CategoryService;
import com.itcast.reggie.service.DishService;
import com.itcast.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService  {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    //根据Id来删除分类,但是在删除之前要判断是否存在外键
    public void remove(Long id) {




        //查询当前菜品是否关联了其他菜品;若已关联,就抛出一个业务异常
        LambdaQueryWrapper<Dish> wrapper1=new LambdaQueryWrapper<>();
            //添加查询条件
            wrapper1.eq(Dish::getCategoryId,id);
            int count1 = dishService.count(wrapper1);
        if (count1>0){
            //已经关联了一个菜品,抛出异常
            throw new CustomException("当前分类下还有关联的菜品,不能删除");
        }


        //查询当前分类是否已经关联套餐,如果已经关联,则抛出一个业务异常
        LambdaQueryWrapper<Setmeal> wrapper2=new LambdaQueryWrapper<>();
            //添加查询条件
            wrapper2.eq(Setmeal::getCategoryId,id);
            int count2 = setmealService.count(wrapper2);
        if (count2>0){
            //已经关联了一个套餐,抛出异常
            throw new CustomException("当前分类下还有关联的套餐,不能删除");
        }


        //正常删除当前分类
        super.removeById(id);

    }
}
