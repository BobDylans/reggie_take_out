package com.itcast.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.reggie.Dto.DishDto;
import com.itcast.reggie.common.R;
import com.itcast.reggie.entity.Category;
import com.itcast.reggie.entity.Dish;
import com.itcast.reggie.entity.DishFlavor;
import com.itcast.reggie.service.CategoryService;
import com.itcast.reggie.service.DishFlavorService;
import com.itcast.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.mbeans.ClassNameMBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.LambdaConversionException;
import java.util.List;
import java.util.stream.Collectors;

/*
Dish的Controller

 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    /*

    新增菜品
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
//        log.info("这里是封装的信息");
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }
    /*
         菜品信息的分页查询
         由于我们的dish文件中缺少一个字段名,所以我们要使用dto类型,用来接收CategoryName属性
         */


    @GetMapping("/page")
    public R<Page<DishDto>> Page(int page, int pageSize, String name){
        //构造分页构造器
        Page<Dish> pageInfo=new Page<>(page,pageSize);
            //创建一个Dto类型的Page
        Page<DishDto> pageInfoDto=new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> wrapper=new LambdaQueryWrapper<>();

        //添加过滤条件
        wrapper.like(name!=null,Dish::getName,name);
        //添加排序查询
        wrapper.orderByDesc(Dish::getSort);

        dishService.page(pageInfo,wrapper);


        //对象拷贝,将我们原本的类型重新拷贝给Dto的page
        //注意,我们要拷贝的是除了recodes(list)之外的其他属性!!!
        BeanUtils.copyProperties(pageInfo,pageInfoDto,"records");
        //因为recodes是一个list类型,用来存储要显示到页面上的信息,我们要先对其进行一些处理

        //取出records
        List<Dish> records = pageInfo.getRecords();


        /**
         * 下面的这个语句的功能是将我们Dish中的records取出,
         * 然后遍历其中的数据,并对他们进行修改
         * 对于空的list<DishDto>,我们要先将相同的属性进行拷贝,再对Dto中独有的属性来赋值
         *
         *
         */

        List<DishDto> list = records.stream().map((item)->{
            //这里的操作在每一次循环中都会执行
            DishDto dishDto=new DishDto();

            //将item中的数据复制给dishDto
            BeanUtils.copyProperties(item,dishDto);

            //获取分类Id,以便来向数据库中查询相对应的菜品名!!
            Long categoryId = item.getCategoryId();

            //根据分类id来获取菜品名,并为dto赋值
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //返回一个组装好的dishDto个体
            return dishDto;
        }).collect(Collectors.toList());
        //最后这步相当于把之前搜集的dto对象重新组合成一个list集合,返回给list<Dto>即可

        pageInfoDto.setRecords(list);

        return R.success(pageInfoDto);
    }

    //和页面进行对应,前端需要什么样的数据,我们就传递给什么数据
    //根据id来查询菜品信息和口味信息
    @GetMapping("/{id}")//以路径拼接的方式来传递信息
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        
//        log.info("这里是封装的信息");
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /*
    根据条件来查询菜品数据
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> wrapper=new LambdaQueryWrapper<>();
        //查询CategoryId相同的数据
        wrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //再向里面添加一个查询条件,即只查询status为1的数据
        wrapper.eq(Dish::getStatus,1);
        //添加一个排序调价
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //执行查询操作
        List<Dish> list = dishService.list(wrapper);

        List<DishDto> dishDtoList = list.stream().map((item)->{
            //这里的操作在每一次循环中都会执行
            DishDto dishDto=new DishDto();

            //将item中的数据复制给dishDto
            BeanUtils.copyProperties(item,dishDto);

            //获取分类Id,以便来向数据库中查询相对应的菜品名!!
            Long categoryId = item.getCategoryId();

            //根据分类id来获取菜品名,并为dto赋值
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> wrapper1=new LambdaQueryWrapper<>();
            wrapper1.eq(DishFlavor::getDishId,dishId);

            //根据dishid来查询flavor
            List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper1);

            dishDto.setFlavors(dishFlavorList);
            //返回一个组装好的dishDto个体
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

}

/*

@GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> wrapper=new LambdaQueryWrapper<>();
        //查询CategoryId相同的数据
        wrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //再向里面添加一个查询条件,即只查询status为1的数据
        wrapper.eq(Dish::getStatus,1);
        //添加一个排序调价
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //执行查询操作
        List<Dish> list = dishService.list(wrapper);


        return R.success(list);
    }




 */


