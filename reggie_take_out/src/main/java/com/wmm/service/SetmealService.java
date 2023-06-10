package com.wmm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wmm.dto.SetmealDto;
import com.wmm.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    // 保存套餐和套餐菜品数据
    public void saveSetmealWithDish(SetmealDto dto);

    // 更新套餐和菜品数据
    public void updateSetmealWithDish(SetmealDto dto);

    // 删除套餐和菜品数据
    public void deleteSetmealWithDish(List<Long> ids);
}
