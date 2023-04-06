package com.sasu.takeout.controller;

import com.sasu.takeout.common.Result;
import com.sasu.takeout.entity.Orders;
import com.sasu.takeout.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;
    @PostMapping("/submit")
    public Result<String> submitOrder(Orders orders){
        orderService.submit(orders);
        return Result.success("下单成功");
    }
}
