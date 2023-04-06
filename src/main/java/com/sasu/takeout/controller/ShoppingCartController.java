package com.sasu.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.sasu.takeout.common.Result;
import com.sasu.takeout.entity.ShoppingCart;
import com.sasu.takeout.service.ShoppingCartService;
import com.sasu.takeout.utils.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        //获取userid
        Long userid = BaseContext.getCurrentId();
        shoppingCart.setUserId(userid);

        Long dishId = shoppingCart.getDishId();

        //判断购物车中是否有重复的菜品，有则数量+1，没有则添加新的
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());

        //判断添加到购物车的是菜品还是套餐
        if (dishId != null){
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);

        if (one != null){
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);
        }else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        return Result.success(one);
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> listResult(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return Result.success(list);
    }

    @DeleteMapping("/clean")
    public Result<String> clean(){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        shoppingCartService.remove(lambdaQueryWrapper);
        return Result.success("购物车清空成功");
    }
}
