package com.wmm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmm.common.CustomException;
import com.wmm.dto.SetmealDto;
import com.wmm.entity.Setmeal;
import com.wmm.entity.SetmealDish;
import com.wmm.mapper.SetmealMapper;
import com.wmm.service.SetmealDishService;
import com.wmm.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 保存套餐和菜品信息
     * @param dto
     */
    @Override
    public void saveSetmealWithDish(SetmealDto dto) {

        // 保存套餐信息
        this.save(dto);

        // 保存菜品信息
        List<SetmealDish> setmealDishes = dto.getSetmealDishes();

        for (SetmealDish dish:setmealDishes) {
            dish.setSetmealId(dto.getId());
        }

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 更新套餐及相关菜品
     * @param dto
     */
    @Override
    public void updateSetmealWithDish(SetmealDto dto) {
        // 更新套餐信息
        this.updateById(dto);

        // 更新菜品信息

        // 删除已有菜品
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(SetmealDish::getSetmealId,dto.getId());

        setmealDishService.remove(wrapper);
        // 更新菜品
        List<SetmealDish> setmealDishes = dto.getSetmealDishes();

        for(SetmealDish dish:setmealDishes){
            dish.setSetmealId(dto.getId());
        }

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 根据id删除套餐
     * @param ids
     */
    @Transactional
    @Override
    public void deleteSetmealWithDish(List<Long> ids) {
        // 检查套餐状态
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();

        setmealWrapper.in(Setmeal::getId,ids);

        setmealWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(setmealWrapper);

        if(count > 0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        // 删除套餐
        this.removeByIds(ids);

        // 删除套餐对应的菜品
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper();

        wrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(wrapper);

    }
}
