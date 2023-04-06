package com.sasu.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sasu.takeout.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
