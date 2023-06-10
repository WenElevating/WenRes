package com.wmm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmm.entity.ShoppingCart;
import com.wmm.mapper.ShoppingCartMapper;
import com.wmm.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
