package com.itcast.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.reggie.common.R;
import com.itcast.reggie.entity.Category;
import com.itcast.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/*
有关分类配置的controller

 */

@Slf4j
@RequestMapping("/category")
@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /*
    一般来说,我们响应给前端的信息不一定是一个完整的数据库表(也就是我们创建的一个entity),根据情况来看即可
    除非是类似于登录的操作,会要求把用户的信息存储在浏览器中,我们需要返回一个entity.


     */
    @PostMapping
    //直接获取post请求发来对的信息,调用save方法就可以添加相关的菜系
    public R<String>  save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page<Category>> page(int page,int pageSize){
        /*固定流程
        1.先创建一个Page<T>类pageInfo,T代表这个页面中的现实的物品的类型.参数是page(页数)和pageSize(页面大小).这两个数据都是由页面传参直接获取的
        2.创建wrapper类,用来存入筛选的信息,可以是排序的信息,也可以是页面上别的信息
        3.调用Service中的page方法,参数就是之前创建的pageInfo和wrapper,这个方法是直接对pageInfo进行改造的,因此可以直接返回
        4.将被填充的的pageInfo放到事前规定好的R类型的数据中并返回

         */
        Page<Category> pageInfo =new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> wrapper=new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,wrapper);

        return R.success(pageInfo);
    }
    @DeleteMapping
    //如果是get请求,信息存储在?后面,那么可以直接通过这来获取哟.但如果是post,就要使用@RequestBody注解来接受啦
    public R<String> delete(Long ids){
        categoryService.remove(ids);


        //categoryService.removeById(ids);

        return R.success("分类菜品删除成功");
    }


    @PutMapping
    //根据Id来修改菜系信息
    //只要能从前端获取到数据,就可以直接放到update的方法中进行修改(其实参数和添加是一样....)
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功!");
    }
    // 根据条件来查询分类数据
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        //创建条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        //添加条件和排序条件
        wrapper.eq(category.getType() != null, Category::getType, category.getType()).orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(wrapper);

        return R.success(list);

    }

}
