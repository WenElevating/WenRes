package com.wmm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wmm.common.R;
import com.wmm.dto.OrdersDto;
import com.wmm.entity.AddressBook;
import com.wmm.entity.OrderDetail;
import com.wmm.entity.Orders;
import com.wmm.entity.User;
import com.wmm.service.AddressBookService;
import com.wmm.service.OrderDetailService;
import com.wmm.service.OrdersService;
import com.wmm.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 提交订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);

        return R.success("下载成功");
    }

    /**
     * 用户订单分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<IPage<OrdersDto>> getByPage(Integer page,Integer pageSize){
        IPage<Orders> ordersIPage = new Page<>(page,pageSize);

        ordersService.page(ordersIPage);

        // 传输类
        IPage<OrdersDto> dtoIPage = new Page<>();

        BeanUtils.copyProperties(ordersIPage,dtoIPage,"records");

        // 复制属性
        List<Orders> records = ordersIPage.getRecords();

        List<OrdersDto> dtoList = new ArrayList<>();

        for (Orders order: records) {

            OrdersDto dto = new OrdersDto();

            BeanUtils.copyProperties(order,dto);

            // 获取用户信息
            User currUser = userService.getById(dto.getUserId());

            dto.setUserName(currUser.getName());

            dto.setEmail(currUser.getEmail());

            // 获取地址信息
            AddressBook address = addressBookService.getById(dto.getAddressBookId());

            dto.setAddress(address.getProvinceName()
                    + address.getCityName()
                    + address.getDistrictName()
            );

            dto.setConsignee(address.getConsignee());

            // 订单详细信息
            LambdaQueryWrapper<OrderDetail> detailWrapper = new LambdaQueryWrapper<>();

            detailWrapper.eq(dto.getNumber() != null,OrderDetail::getOrderId,Long.valueOf(dto.getNumber()));

            List<OrderDetail> details = orderDetailService.list(detailWrapper);

            dto.setOrderDetails(details);

            dtoList.add(dto);
        }

        dtoIPage.setRecords(dtoList);

        return R.success(dtoIPage);
    }

    @GetMapping("/page")
    public R<IPage<Orders>> getByOrderPage(Integer page, Integer pageSize, Long number, String beginTime,String endTime){
        IPage<Orders> ordersIPage = new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders> ordersWrapper = new LambdaQueryWrapper<>();

        ordersWrapper.eq(number!=null,Orders::getNumber,number);

        if(beginTime != null && endTime != null){
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            ordersWrapper.between(Orders::getOrderTime,LocalDateTime.parse(beginTime,df),LocalDateTime.parse(endTime,df));
        }

        ordersWrapper.orderByDesc(Orders::getOrderTime);

        ordersService.page(ordersIPage,ordersWrapper);

        return R.success(ordersIPage);
    }
}
