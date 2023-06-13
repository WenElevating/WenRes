package com.wmm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmm.common.CustomException;
import com.wmm.entity.*;
import com.wmm.mapper.OrdersMapper;
import com.wmm.service.*;
import com.wmm.utils.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Transactional
    @Override
    public void submit(Orders orders) {
        // 获取用户id
        Long currentId = BaseContext.getCurrentId();

        // 查询购物车
        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper = new LambdaQueryWrapper<>();

        shoppingCartWrapper.eq(currentId!=null,ShoppingCart::getUserId,currentId);

        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartWrapper);

        if(shoppingCartList == null || shoppingCartList.size() == 0){
            throw new CustomException("购物车为空，不能下单");
        }
        // 获取用户信息
        User user = userService.getById(currentId);

        if(user == null){
            throw new CustomException("用户不存在，请先登录");
        }

        // 获取地址数据
        AddressBook address = addressBookService.getById(orders.getAddressBookId());

        if(address == null){
            throw new CustomException("地址信息有误，不能下单");
        }

        // 保存订单
        long orderId = IdWorker.getId();

        // 计算订单金额
        List<OrderDetail> detailList = new ArrayList<>();

        AtomicInteger amount = new AtomicInteger(0);
        for(ShoppingCart cart : shoppingCartList){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(cart.getNumber());
            orderDetail.setDishFlavor(cart.getDishFlavor());
            orderDetail.setDishId(cart.getDishId());
            orderDetail.setSetmealId(cart.getSetmealId());
            orderDetail.setName(cart.getName());
            orderDetail.setImage(cart.getImage());
            orderDetail.setAmount(cart.getAmount());
            amount.addAndGet(cart.getAmount().multiply(new BigDecimal(cart.getNumber())).intValue());
            detailList.add(orderDetail);
        }

        orders.setNumber(String.valueOf(orderId));

        orders.setStatus(1);

        orders.setUserId(currentId);

        orders.setUserName(user.getName());

        orders.setOrderTime(LocalDateTime.now());

        orders.setCheckoutTime(LocalDateTime.now());

        orders.setEmail(user.getEmail());

        orders.setConsignee(address.getConsignee());

        orders.setAmount(new BigDecimal(amount.get()));

        orders.setAddress((address.getProvinceName() != null ? address.getProvinceName():"")
                + (address.getCityName() != null ? address.getCityName(): "")
                + (address.getDistrictName() != null ? address.getDistrictName(): "")
                + (address.getDetail() != null ? address.getDetail(): "")
        );

        this.save(orders);

        // 清空购物车
        shoppingCartService.remove(shoppingCartWrapper);

        orderDetailService.saveBatch(detailList);

    }
}
