package com.itcast.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itcast.reggie.entity.Orders;
import org.springframework.core.annotation.Order;

public interface OrdersService extends IService<Orders> {


    public void submit(Orders orders);
}
