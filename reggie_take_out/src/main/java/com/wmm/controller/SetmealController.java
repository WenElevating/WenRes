package com.wmm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wmm.common.R;
import com.wmm.dto.SetmealDto;
import com.wmm.entity.Category;
import com.wmm.entity.Setmeal;
import com.wmm.entity.SetmealDish;
import com.wmm.service.CategoryService;
import com.wmm.service.SetmealDishService;
import com.wmm.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐
     * @param dto
     * @return
     */
    @PostMapping
    public R<String> addSetmeal(@RequestBody SetmealDto dto){
        log.info("dto = {}",dto);
        setmealService.saveSetmealWithDish(dto);
        return R.success("新增套餐成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<IPage> getSetmealByPage(Integer page, Integer pageSize, String name){
        IPage<Setmeal>mealPage = new Page(page,pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(mealPage,queryWrapper);

        List<Setmeal> records = mealPage.getRecords();

        List<SetmealDto> dtoList = new ArrayList<>();

        for (Setmeal meal:records) {
            SetmealDto dto = new SetmealDto();

            BeanUtils.copyProperties(meal,dto);

            Category cate = categoryService.getById(dto.getCategoryId());

            dto.setCategoryName(cate.getName());

            dtoList.add(dto);
        }

        IPage<SetmealDto> dtoIPage = new Page<>();

        BeanUtils.copyProperties(mealPage,dtoIPage);

        dtoIPage.setRecords(dtoList);

        return R.success(dtoIPage);
    }

    /**
     * 根据id获取套餐信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<SetmealDto> getSetmealById(@PathVariable Long id){
        log.info("id = {}",id);

        SetmealDto dto = new SetmealDto();

        Setmeal meal = setmealService.getById(id);

        // 复制属性
        BeanUtils.copyProperties(meal,dto);

        // 查询套餐对应的菜品
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(SetmealDish::getSetmealId,dto.getId());

        List<SetmealDish> setmealDishs = setmealDishService.list(wrapper);

        dto.setSetmealDishes(setmealDishs);

        // 获取套餐分类名称
        Category cate = categoryService.getById(dto.getCategoryId());

        dto.setCategoryName(cate.getName());

        if(meal == null){
            return R.error(null);
        }
        return R.success(dto);
    }

    /**
     * 更新套餐
     * @param dto
     * @return
     */
    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto dto){
        log.info("update dto = {}",dto);

        setmealService.updateSetmealWithDish(dto);

        return R.success("套餐更新成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam List<Long> ids){
        log.info("ids = {}",ids);

        setmealService.deleteSetmealWithDish(ids);

        return R.success("套餐数据删除成功");
    }


    @GetMapping("/list")
    public R<List<Setmeal>> getSetmealByCategoryId(Setmeal setmeal){
        log.info("setmeal = {}",setmeal);

        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());

        wrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());

        wrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> mealList = setmealService.list(wrapper);

        return R.success(mealList);
    }
}
