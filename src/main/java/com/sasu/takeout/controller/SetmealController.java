package com.sasu.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasu.takeout.common.Result;
import com.sasu.takeout.dto.DishDto;
import com.sasu.takeout.dto.SetmealDto;
import com.sasu.takeout.entity.*;
import com.sasu.takeout.service.CategoryService;
import com.sasu.takeout.service.SetmealDishService;
import com.sasu.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.basic.BasicListUI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    SetmealService setmealService;
    @Autowired
    CategoryService categoryService;

    @PostMapping
    public Result<String> addSetmealWithDish(@RequestBody SetmealDto setmealDto){
        log.info("category_id,{}",setmealDto.getCategoryId());
        setmealService.saveSetmealWithDish(setmealDto);
        return Result.success("套餐保存成功");
    }

    @GetMapping("/page")
    public Result<Page> pageSetmeal(int page,int pageSize,String name){

        Page<Setmeal> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null,Setmeal::getName,name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,lambdaQueryWrapper);

        Page<SetmealDto> dtoPage = new Page<>(page,pageSize);

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> setmealDtoList = null;
        setmealDtoList = records.stream().map( items -> {
            SetmealDto setmealDto = new SetmealDto();
            setmealDto.setCategoryName(categoryService.getById(items.getCategoryId()).getName());
            BeanUtils.copyProperties(items,setmealDto);
            return setmealDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        dtoPage.setRecords(setmealDtoList);

        return Result.success(dtoPage);
    }

    @GetMapping("/{id}")
    public Result<SetmealDto> getSetmeal(@PathVariable Long id){

        SetmealDto setmealDto = setmealService.getSetmealWithDish(id);

        return Result.success(setmealDto);
    }

    @DeleteMapping
    public Result<String> deleteSetmealWithDish(@RequestParam List<Long> ids){
        setmealService.removeSetmealWithDish(ids);
        return Result.success("删除成功");
    }

    @PutMapping
    public Result<String> updateSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.updateSetmealWithDish(setmealDto);
        return Result.success("修改成功");
    }

    @PostMapping("/status/0")
    @Transactional
    public Result<String> haltSales(@RequestParam List<Long> ids){
       LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
       lambdaQueryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);

        list = list.stream().map( items -> {
            items.setStatus(0);
            return items;
        }).collect(Collectors.toList());

        setmealService.updateBatchById(list);

        return Result.success("停售成功");
    }

    @PostMapping("/status/1")
    @Transactional
    public Result<String> launchSale(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);

        list = list.stream().map( items -> {
            items.setStatus(1);
            return items;
        }).collect(Collectors.toList());

        setmealService.updateBatchById(list);

        return Result.success("启售成功");
    }

    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId + '_' + #setmeal.status")//通过#来获取方法参数
    public Result<List<Setmeal>> getList(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,1);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        return Result.success(list);
    }

}
