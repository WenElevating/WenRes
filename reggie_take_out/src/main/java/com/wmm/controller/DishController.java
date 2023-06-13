package com.wmm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wmm.common.CustomException;
import com.wmm.common.R;
import com.wmm.dto.DishDto;
import com.wmm.entity.Category;
import com.wmm.entity.Dish;
import com.wmm.entity.DishFlavor;
import com.wmm.service.CategoryService;
import com.wmm.service.DishFlavorService;
import com.wmm.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
* 菜品管理
* */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService flavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询菜品
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<IPage> getDishByPage(Integer page,Integer pageSize,String name){
        IPage<Dish> dishPage = new Page<>(page,pageSize);

        LambdaQueryWrapper<Dish>dishWrapper = new LambdaQueryWrapper();

        dishWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);

        dishWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(dishPage,dishWrapper);

        IPage<DishDto> dtoIPage =new Page<>();

        // 对象拷贝 拷贝属性
        BeanUtils.copyProperties(dishPage,dtoIPage,"records");

        List<Dish> records = dishPage.getRecords();

        List<DishDto> dtoRecords = new ArrayList<>(records.size());

        for(Dish list: records){

            Category cate = categoryService.getById(list.getCategoryId());

            DishDto dto = new DishDto();

            dto.setCategoryName(cate.getName());

            // 拷贝属性
            BeanUtils.copyProperties(list,dto);

            dtoRecords.add(dto);
        }

        dtoIPage.setRecords(dtoRecords);

        return R.success(dtoIPage);
    }

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){
        log.info("dish = {}",dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }

    /**
     * 查询菜品信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<DishDto> getDishById(@PathVariable Long id){
        log.info("id = {}",id);


        DishDto dto = dishService.getDishWithFlavor(id);

        if(dto != null){
            return R.success(dto);
        }

        return R.error(null);
    }

    /**
     * 更新菜品信息
     * @param dto
     * @return
     */
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dto){
        log.info("dto = {}",dto);

        dishService.updateDishWithFlavor(dto);

        return R.success("修改菜品成功");
    }

    /**
     * 根据套餐id获取菜品列表
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> getDishByCategoryId(Dish dish){
        log.info("categoryId = {}",dish.getCategoryId());

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.isNotEmpty(dish.getName()),Dish::getName,dish.getName());

        wrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());

        wrapper.eq(Dish::getStatus,1);

        wrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(wrapper);

        List<DishDto> dtoList = new ArrayList<>();

        for(Dish curr : list){
            DishDto dto = new DishDto();

            BeanUtils.copyProperties(curr,dto);

            // 获取菜品口味
            LambdaQueryWrapper<DishFlavor> flavorWrapper = new LambdaQueryWrapper<>();

            flavorWrapper.eq(DishFlavor::getDishId,curr.getId());

            List<DishFlavor> currList = flavorService.list(flavorWrapper);

            dto.setFlavors(currList);

            // 获取菜品分类名称
            Category cate = categoryService.getById(dto.getCategoryId());

            if(cate != null){
                dto.setCategoryName(cate.getName());
            }

            dtoList.add(dto);
        }

        return R.success(dtoList);
    }

    /**
     * 菜品停售
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> updateStatusStop(@RequestParam List<Long> ids){
        log.info("0 ids = {}",ids);

        // 修改状态
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();


        dishWrapper.in(ids != null,Dish::getId,ids);

        Dish dish = new Dish();

        dish.setStatus(0);

        dishService.update(dish,dishWrapper);

        return R.success("菜品已停售");
    }

    /**
     * 菜品起售
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> updateStatusStart(@RequestParam List<Long> ids){
        log.info("0 ids = {}",ids);

        // 修改状态
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();


        dishWrapper.in(ids != null,Dish::getId,ids);

        Dish dish = new Dish();

        dish.setStatus(1);

        dishService.update(dish,dishWrapper);

        return R.success("菜品已起售");
    }

    @DeleteMapping
    public R<String> removeDish(@RequestParam List<Long> ids){
        // 判断菜品是否停售
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();

        dishWrapper.in(Dish::getId,ids);

        dishWrapper.eq(Dish::getStatus,1);

        int count = dishService.count(dishWrapper);

        if(count > 0){
            throw new CustomException("菜品正在售卖，请先停售");
        }

        dishService.removeByIds(ids);

        return R.success("删除菜品成功");
    }
}
