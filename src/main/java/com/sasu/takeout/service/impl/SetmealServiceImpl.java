package com.sasu.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sasu.takeout.common.CustomException;
import com.sasu.takeout.dto.DishDto;
import com.sasu.takeout.dto.SetmealDto;
import com.sasu.takeout.entity.Setmeal;
import com.sasu.takeout.entity.SetmealDish;
import com.sasu.takeout.mapper.SetmealMapper;
import com.sasu.takeout.service.SetmealDishService;
import com.sasu.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealDishService setmealDishService;
    @Override
    @Transactional
    public void saveSetmealWithDish(SetmealDto setmealDto) {

        log.info("setmealDto,{}",setmealDto.getCategoryId());
        this.save(setmealDto);

        List<SetmealDish> list = setmealDto.getSetmealDishes();
        list = list.stream().map(items -> {
            items.setSetmealId(setmealDto.getId());
            return items;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(list);

    }


    @Override
    @Transactional
    public void removeSetmealWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(lambdaQueryWrapper);
        if (count > 0){
            throw new CustomException("套餐正在销售中，禁止删除");
        }

        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(dishLambdaQueryWrapper);

        this.removeByIds(ids);
    }

    @Override
    @Transactional
    public void updateSetmealWithDish(SetmealDto setmealDto) {

        //修改套餐的基本信息
        this.updateById(setmealDto);

        //删除菜品
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(lambdaQueryWrapper);
        //新增菜品
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        list = list.stream().map(items -> {
            items.setSetmealId(setmealDto.getId());
            return items;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(list);
    }

    @Override
    public SetmealDto getSetmealWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, SetmealDish::getSetmealId, id);

        if (setmeal != null) {
            BeanUtils.copyProperties(setmeal, setmealDto);

            List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(dishes);

            return setmealDto;
        }

        return null;
    }
}
