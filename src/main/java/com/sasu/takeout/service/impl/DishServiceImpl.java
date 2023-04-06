package com.sasu.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sasu.takeout.common.CustomException;
import com.sasu.takeout.dto.DishDto;
import com.sasu.takeout.entity.Dish;
import com.sasu.takeout.entity.DishFlavor;
import com.sasu.takeout.entity.Setmeal;
import com.sasu.takeout.entity.SetmealDish;
import com.sasu.takeout.mapper.DishMapper;
import com.sasu.takeout.service.DishFlavorService;
import com.sasu.takeout.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;

    /**
     * 菜品保存涉及两张表的操作，dish基本信息，dish口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveDish(DishDto dishDto) {

        //保存dish基本信息,调用此方法后，dishId就被赋值了,但DishFlavor中的dishId没有封装赋值
        this.save(dishDto);
        //获取dishID
        Long dishId = dishDto.getId();

        List<DishFlavor> dishFlavor = dishDto.getFlavors();
        dishFlavor = dishFlavor.stream().map(items -> {
            items.setDishId(dishId);
            return items;
        }).collect(Collectors.toList());


        dishFlavorService.saveBatch(dishFlavor);


    }

    @Override
    public DishDto getDishByIdWithFlavor(Long id) {
        //查询菜品的基本信息
        Dish dish = this.getById(id);

        //查询口味信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavor = dishFlavorService.list(lambdaQueryWrapper);

        //将dish对象中的基本信息拷贝到dishDto中
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        dishDto.setFlavors(flavor);

        return dishDto;
    }

    @Override
    public void updateDishWithFlavor(DishDto dishDto) {
        //更新dish表
        this.updateById(dishDto);
        //删除口味
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(lambdaQueryWrapper);

        //新增口味
        List<DishFlavor> dishFlavor = dishDto.getFlavors();
        dishFlavor = dishFlavor.stream().map(items -> {
            items.setDishId(dishDto.getId());
            return items;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(dishFlavor);
    }

    @Override
    @Transactional
    public void removeDishWithFlavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Dish::getId,ids);
        lambdaQueryWrapper.eq(Dish::getStatus,1);

        int count = this.count(lambdaQueryWrapper);
        if (count > 0){
            throw new CustomException("套餐正在销售中，禁止删除");
        }

        LambdaQueryWrapper<DishFlavor> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(DishFlavor::getDishId,ids);

        dishFlavorService.remove(dishLambdaQueryWrapper);

        this.removeByIds(ids);
    }
}
