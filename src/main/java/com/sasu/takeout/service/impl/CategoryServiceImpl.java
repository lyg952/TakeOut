package com.sasu.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sasu.takeout.common.CustomException;
import com.sasu.takeout.common.Result;
import com.sasu.takeout.entity.Category;
import com.sasu.takeout.entity.Dish;
import com.sasu.takeout.entity.Setmeal;
import com.sasu.takeout.mapper.CategoryMapper;
import com.sasu.takeout.service.CategoryService;
import com.sasu.takeout.service.DishService;
import com.sasu.takeout.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    DishService dishService;
    @Autowired
    SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dish::getCategoryId,id);

        LambdaQueryWrapper<Setmeal> lambdaWrapper = new LambdaQueryWrapper<>();
        lambdaWrapper.eq(Setmeal::getCategoryId,id);

        int dishCount = dishService.count(lambdaQueryWrapper);
        int setMealCount = setmealService.count(lambdaWrapper);

        if (dishCount > 0){
           throw new CustomException("该分类已关联菜品 ，不能删除！！！！");
        }
        if (setMealCount > 0){
            throw new CustomException("该分类已关联套餐 ，不能删除！！！！");
        }
        super.removeById(id);
    }

}
