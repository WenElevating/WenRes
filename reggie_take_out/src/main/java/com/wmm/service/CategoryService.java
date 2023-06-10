package com.wmm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wmm.entity.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
