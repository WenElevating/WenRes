package com.wmm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmm.entity.DishFlavor;
import com.wmm.mapper.DishFlavorMapper;
import com.wmm.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
