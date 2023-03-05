package com.itcast.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.reggie.Dto.SetmealDto;
import com.itcast.reggie.common.CustomException;
import com.itcast.reggie.entity.Setmeal;
import com.itcast.reggie.entity.SetmealDish;
import com.itcast.reggie.mapper.SetmealMapper;
import com.itcast.reggie.service.DishService;
import com.itcast.reggie.service.SetmealDishService;
import com.itcast.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /*

    新增套餐,同时需要保存套餐和菜品的关系
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息,操作setmeal表
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());



        //保存套餐和菜品的关联,操作setmeal_dish表
        setmealDishService.saveBatch(setmealDishes);


    }

    /*
    删除套餐

     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
//        只有停售状态才能删除.
        LambdaQueryWrapper<Setmeal> wrapper=new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId,ids);
        wrapper.eq(Setmeal::getStatus,1);
//        如果不能删除,则抛出一个异常
        int count = this.count(wrapper);
        if(count>0){
            throw new CustomException("套餐正在售卖中,不能删除");
        }

        //如果可以删除,先删除套餐表中的数据---setmeal
        this.removeByIds(ids);

        //删除关系表中的数据---setmeal_dish
        LambdaQueryWrapper<SetmealDish> Qwrapper=new LambdaQueryWrapper<>();
        Qwrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(Qwrapper);

    }

}
