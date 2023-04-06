package com.sasu.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sasu.takeout.dto.DishDto;
import com.sasu.takeout.entity.Dish;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DishService extends IService<Dish> {

    public void saveDish(DishDto dishDto);

    public DishDto getDishByIdWithFlavor(Long id);

    void updateDishWithFlavor(DishDto dishDto);

    void removeDishWithFlavor(List<Long> ids);
}
