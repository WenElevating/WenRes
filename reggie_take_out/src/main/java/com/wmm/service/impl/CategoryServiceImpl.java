package com.wmm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmm.common.CustomException;
import com.wmm.entity.Category;
import com.wmm.entity.Dish;
import com.wmm.entity.Setmeal;
import com.wmm.mapper.CategoryMapper;
import com.wmm.mapper.DishMapper;
import com.wmm.mapper.SetmealMapper;
import com.wmm.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 根据id删除分类，删除之前需要判断是否关联菜品或套餐
     * @param id
     */
    @Override
    public void remove(Long id) {
        // 检查分类是否关联菜品
        LambdaQueryWrapper<Dish>dishWrapper = new LambdaQueryWrapper();

        dishWrapper.eq(Dish::getCategoryId,id);

        Integer dishCount = dishMapper.selectCount(dishWrapper);

        if(dishCount > 0){
            throw new CustomException("当前分类关联了菜品,不能删除");
        }

        // 检查分类是否关联菜单
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();

        setmealWrapper.eq(Setmeal::getCategoryId,id);

        Integer mealCount = setmealMapper.selectCount(setmealWrapper);

        if(mealCount > 0){
            throw new CustomException("当前分类关联了套餐,不能删除");
        }

        // 正常删除
        super.removeById(id);
    }
}
