package com.itcast.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itcast.reggie.Dto.SetmealDto;
import com.itcast.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {


    public void saveWithDish(SetmealDto setmealDto);
/*

删除套餐
 */
    public void removeWithDish(List<Long> ids);
}
