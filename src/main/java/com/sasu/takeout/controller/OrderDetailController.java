package com.sasu.takeout.controller;


import com.sasu.takeout.common.Result;
import com.sasu.takeout.entity.Orders;
import com.sasu.takeout.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Autowired
    private OrderService ordersService;

    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        //比较繁琐，在service实现
        ordersService.submit(orders);
        return Result.success("下单成功");
    }
}
