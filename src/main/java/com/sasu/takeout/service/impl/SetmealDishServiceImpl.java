package com.sasu.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sasu.takeout.entity.SetmealDish;
import com.sasu.takeout.mapper.SetmealDishMapper;
import com.sasu.takeout.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
