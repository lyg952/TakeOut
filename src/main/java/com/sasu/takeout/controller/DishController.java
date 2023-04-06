package com.sasu.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasu.takeout.common.Result;
import com.sasu.takeout.dto.DishDto;
import com.sasu.takeout.entity.Category;
import com.sasu.takeout.entity.Dish;
import com.sasu.takeout.entity.DishFlavor;
import com.sasu.takeout.service.CategoryService;
import com.sasu.takeout.service.DishFlavorService;
import com.sasu.takeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public Result<Page> dishPage(int page,int pageSize,String name){

        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage  = new Page<>(page,pageSize);

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null,Dish::getName,name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,lambdaQueryWrapper);

        //获取响应数据集合
        List<Dish> records = pageInfo.getRecords();
        //遍历集合并给集合里的数据复制
        List<DishDto> dishDtoList = records.stream().map(items -> {
            DishDto dishDto = new DishDto();

            Long categoryId = items.getCategoryId();
            Category category = categoryService.getById(categoryId);

            dishDto.setCategoryName(category.getName());

            //将itmes中的其他数据拷贝到dishDto中
            BeanUtils.copyProperties(items,dishDto);
            return dishDto;
        }).collect(Collectors.toList());


        //将pageinfo对象中的数据拷贝到dishDtoPage对象中去，排除属性records不拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        dishDtoPage.setRecords(dishDtoList);


        return Result.success(dishDtoPage);
    }

    @PostMapping
    public Result<String> dishAdd(@RequestBody DishDto dishDto){
        log.info("dishDto：{}",dishDto);
        dishService.saveDish(dishDto);
        return Result.success("菜品添加成功");
    }

    @GetMapping("/{id}")
    public Result<DishDto> updateDish(@PathVariable Long id){

        DishDto dishDto = dishService.getDishByIdWithFlavor(id);
        return Result.success(dishDto);
    }

    @PutMapping
    public Result<String> dishUpdate(@RequestBody DishDto dishDto){

        dishService.updateDishWithFlavor(dishDto);

        return Result.success("菜品添加成功");
    }

   /* @GetMapping("/list")
    public Result<List<Dish>> listResult(Dish dish){
        log.info("列表查询{}",dish.getCategoryId());
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        return Result.success(list);
    }*/

    @GetMapping("/list")
    public Result<List<DishDto>> listResult(Dish dish){


        log.info("列表查询{}",dish.getCategoryId());
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);

        List<DishDto> dishDtoList = list.stream().map(items -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(items,dishDto);

            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,items.getId());

            List<DishFlavor> list1 = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());
        return Result.success(dishDtoList);
    }

    @PostMapping("/status/0")
    @Transactional
    public Result<String> haltSales(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.in(Dish::getId,ids);

        /*Dish dishServiceOne = dishService.getOne(lambdaQueryWrapper);
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishServiceOne,dish,"status");

        dish.setStatus(0);*/
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        list = list.stream().map(items ->{
            items.setStatus(0);
            return items;
        }).collect(Collectors.toList());
        dishService.updateBatchById(list);
//        dishService.update(list,lambdaQueryWrapper);
        return Result.success("停售成功");
    }

    @PostMapping("/status/1")
    @Transactional
    public Result<String> launchSale(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.in(Dish::getId,ids);
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        list = list.stream().map(items -> {
            items.setStatus(1);
            return items;
        }).collect(Collectors.toList());
        dishService.updateBatchById(list);


        return Result.success("启售成功");
    }

    @DeleteMapping
    public Result<String> deleteDishWithFlavor(@RequestParam List<Long> ids){
        dishService.removeDishWithFlavor(ids);
        return Result.success("删除成功");
    }
}
