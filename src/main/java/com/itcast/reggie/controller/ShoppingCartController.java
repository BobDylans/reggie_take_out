package com.itcast.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcast.reggie.common.BaseContext;
import com.itcast.reggie.common.R;
import com.itcast.reggie.entity.ShoppingCart;
import com.itcast.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.server.WsHttpUpgradeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.beans.beancontext.BeanContext;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {


    @Autowired
    private ShoppingCartService shoppingCartService;



    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("成功接收到了购物车的信息");
        //设置用户Id,指定当前是哪个用户的的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询一下当前添加的菜品或套餐是否存在与购物车中
        Long dishId = shoppingCart.getDishId();
        //相当于每一个用户都拥有一列数据,我们要先查出用户的id(通过存储再会话中的信息获取)然后再去查询菜品的信息
        LambdaQueryWrapper<ShoppingCart> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,currentId);


        //这一步用来确定用户添加的是套餐还是菜品
        if(dishId!=null){
            //当前提交的是dish菜品
            wrapper.eq(ShoppingCart::getDishId,dishId);

        }else {
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }


        //查看当前菜品是否为空,如果不为空就说明购物车中已经存在
        ShoppingCart one = shoppingCartService.getOne(wrapper);

        if(one!=null){
            //如果已经存在,就种子数量基础上加一
            Integer number = one.getNumber();
            one.setNumber(number+1);
            shoppingCartService.updateById(one);
        }else{
            //如果不存在,则添加到购物车中,默认是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            //跑到这个分支,就说明查询的商品再购物车中比能不存在,因此我们可以把它赋值给one,以便统一返回
            one=shoppingCart;
        }

        return R.success(one);
    }

    //查看购物车
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){

        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> wrapper=new LambdaQueryWrapper<>();
        Long currentId = BaseContext.getCurrentId();
        wrapper.eq(ShoppingCart::getUserId,currentId).orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);

        return R.success(list);
    }
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("成功接收到了购物车的信息");
        //设置用户Id,指定当前是哪个用户的的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询一下当前添加的菜品或套餐是否存在与购物车中
        Long dishId = shoppingCart.getDishId();
        //相当于每一个用户都拥有一列数据,我们要先查出用户的id(通过存储再会话中的信息获取)然后再去查询菜品的信息
        LambdaQueryWrapper<ShoppingCart> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,currentId);


        //这一步用来确定用户添加的是套餐还是菜品
        if(dishId!=null){
            //当前提交的是dish菜品
            wrapper.eq(ShoppingCart::getDishId,dishId);

        }else {
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }


        //查看当前购物车是否为空,如果不为空就说明购物车中已经存在
        ShoppingCart one = shoppingCartService.getOne(wrapper);

        if(one.getNumber()>=1){
            //如果已经存在,就种子数量基础上减一
            Integer number = one.getNumber();
            one.setNumber(number-1);
            shoppingCartService.updateById(one);
        }else if (one.getNumber()==0){

            shoppingCartService.removeById(one);
            return R.error("你还没点这个菜品");
        }else {

            return R.error("你还没点这个菜品");
        }

        return R.success("成功");
    }
    //清空购物车
    @DeleteMapping("/clean")
    public R<String> clean(){


        LambdaQueryWrapper<ShoppingCart> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(wrapper);

        return R.success("清空购物车成功");
    }

}
/*
@GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }



 */