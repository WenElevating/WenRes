package com.wmm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmm.entity.OrderDetail;
import com.wmm.mapper.OrderDetailMapper;
import com.wmm.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
