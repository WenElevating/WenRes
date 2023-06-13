package com.wmm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wmm.common.R;
import com.wmm.entity.Category;
import com.wmm.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
*  分类管理
*
* */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分类分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<IPage> QueryCategoryByPage(Integer page,Integer pageSize){
        IPage<Category> categoryPage = new Page<>(page,pageSize);

        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();

        wrapper.orderByAsc(Category::getSort);

        categoryService.page(categoryPage,wrapper);

        return R.success(categoryPage);
    }

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> saveCategory(@RequestBody Category category){
        log.info("category = {}",category.toString());

        categoryService.save(category);

        return R.success("新增分类成功");
    }

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteCategory(Long ids){
        log.info("ids = {}",ids);

        // 判断当前分类是否关联了菜品或套餐

        // categoryService.removeById(ids);
        categoryService.remove(ids);

        return R.success("分类删除成功");
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category){
        log.info("category = {}",category.toString());

        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询分类
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> getCategoryList(Category category){
        LambdaQueryWrapper<Category>queryWrapper = new LambdaQueryWrapper();

        // 添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());

        // 排序条件 根据顺序排序，顺序相同根据修改时间排序
        queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);

        List<Category> categories = categoryService.list(queryWrapper);

        if(categories == null){
            return R.error(null);
        }

        return R.success(categories);
    }
}
