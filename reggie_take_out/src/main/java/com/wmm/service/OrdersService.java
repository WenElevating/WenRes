package com.wmm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wmm.entity.Orders;

public interface OrdersService extends IService<Orders> {
    // 用户下单
    public void submit(Orders orders);
}
