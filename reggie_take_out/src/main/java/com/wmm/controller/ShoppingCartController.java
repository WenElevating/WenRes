package com.wmm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wmm.common.R;
import com.wmm.entity.ShoppingCart;
import com.wmm.service.ShoppingCartService;
import com.wmm.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("shoppingCart = {}",shoppingCart);
        Long currentId = BaseContext.getCurrentId();

        // 设置用户id
        shoppingCart.setUserId(currentId);

        // 查询当前菜品或菜单是否在数据库中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper = new LambdaQueryWrapper<>();

        if(dishId != null){
            // 当前为菜品
            shoppingCartWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            shoppingCartWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart shopCart = shoppingCartService.getOne(shoppingCartWrapper);


        if(shopCart != null){

            shopCart.setNumber(shopCart.getNumber()+1);

            shoppingCartService.updateById(shopCart);
        }else{

            shoppingCart.setNumber(1);

            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCartService.save(shoppingCart);

            shopCart = shoppingCart;
        }

        return R.success(shopCart);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> getShoppingCartList(){

        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(currentId != null,ShoppingCart::getUserId,currentId);

        wrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(wrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(currentId!=null,ShoppingCart::getUserId,currentId);

        shoppingCartService.remove(wrapper);

        return R.success("清空购物车成功");
    }
}
