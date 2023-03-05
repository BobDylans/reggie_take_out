package com.itcast.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.reggie.Dto.DishDto;
import com.itcast.reggie.common.R;
import com.itcast.reggie.entity.Dish;
import com.itcast.reggie.entity.DishFlavor;
import com.itcast.reggie.mapper.DishMapper;
import com.itcast.reggie.service.DishFlavorService;
import com.itcast.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
    /*
        新增菜品,同时插入菜品所对应的口味数据,需要操作两张表:dish,dish_flavor

     */

        //保存菜品的基本信息到菜品表
        this.save(dishDto);


        Long dishId = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();


        flavors=flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味表到DishFlavor
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //先创建一个空的dishDto对象,用来接收数据
        DishDto dishDto=new DishDto();

        //查询菜品基本信息,从dish表中查询
        Dish dish = this.getById(id);

        //将普通的属性进行赋值给dishDto
        BeanUtils.copyProperties(dish,dishDto);


        //查询当前菜品对应的口味信息,从dish_flavor表中查询
        LambdaQueryWrapper<DishFlavor> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(wrapper);


        //将查到的信息再赋值给dishDto即可
        dishDto.setFlavors(list);
        return dishDto;

    }

    @Override
    //修改菜品信息时,同时修改口味信息
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表的基本信息(由于dishDto是Dish的子类,所以这个方法会更新一些重合的字段)
        this.updateById(dishDto);

        //先清理当前菜品的口味数据--dish_flavor表中的delete操作
        Long id = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,id);
        dishFlavorService.remove(wrapper);

        //再添加当前菜品提交过来的口味数据--dish_flavor表中的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
            //注意要处理一下注意一下id的问题,因为不会自动向其中添加id的信息,所以我们要手动进行添加
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);


    }


}
