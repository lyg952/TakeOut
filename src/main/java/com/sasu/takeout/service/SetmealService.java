package com.sasu.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sasu.takeout.dto.DishDto;
import com.sasu.takeout.dto.SetmealDto;
import com.sasu.takeout.entity.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SetmealService extends IService<Setmeal> {
    void saveSetmealWithDish(SetmealDto setmealDto);

    SetmealDto getSetmealWithDish(Long id);

    void removeSetmealWithDish(List<Long> ids);

    void updateSetmealWithDish(SetmealDto setmealDto);
}
