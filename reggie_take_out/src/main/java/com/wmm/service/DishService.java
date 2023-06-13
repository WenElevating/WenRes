package com.wmm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wmm.dto.DishDto;
import com.wmm.entity.Dish;

public interface DishService extends IService<Dish> {
    // 新增菜品同时加入对应的口味数据
    public void saveWithFlavor(DishDto dishDto);

    // 获取菜品信息同时获取对应的口味
    public DishDto getDishWithFlavor(Long id);

    // 更新菜品信息和口味数据
    public void updateDishWithFlavor(DishDto dto);
}
