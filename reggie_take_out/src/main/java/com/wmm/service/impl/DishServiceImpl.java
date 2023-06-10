package com.wmm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmm.dto.DishDto;
import com.wmm.entity.Dish;
import com.wmm.entity.DishFlavor;
import com.wmm.mapper.DishFlavorMapper;
import com.wmm.mapper.DishMapper;
import com.wmm.service.DishFlavorService;
import com.wmm.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService flavorService;
    /**
     * 新增菜品同时保留口味信息
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息
        this.save(dishDto);

        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        for (DishFlavor fla: flavors) {
            fla.setDishId(dishId);
        }
        // 保存菜品口味
        flavorService.saveBatch(flavors);
    }

    /**
     * 根据菜品id查询菜品信息
     * @param id
     * @return
     */
    @Override
    public DishDto getDishWithFlavor(Long id) {
        // 查询菜品信息
        Dish dish = this.getById(id);

        DishDto dto = new DishDto();

        // 属性复制
        BeanUtils.copyProperties(dish,dto);

        // 根据菜品id查询口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(DishFlavor::getDishId,id);

        List<DishFlavor> list = flavorService.list(queryWrapper);

        dto.setFlavors(list);

        return dto;
    }

    /**
     * 更新菜品
     * @param dto
     */
    @Override
    public void updateDishWithFlavor(DishDto dto) {
        // 更新菜品
        this.updateById(dto);

        // 清除口味
        LambdaQueryWrapper<DishFlavor>queryWrapper = new LambdaQueryWrapper();

        queryWrapper.eq(DishFlavor::getDishId,dto.getId());

        flavorService.remove(queryWrapper);

        // 更新口味
        List<DishFlavor> flavors = dto.getFlavors();

        for (DishFlavor fa:flavors) {

            fa.setDishId(dto.getId());
        }

        flavorService.saveBatch(flavors);
    }
}
