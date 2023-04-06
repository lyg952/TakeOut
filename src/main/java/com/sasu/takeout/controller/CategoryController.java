package com.sasu.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasu.takeout.common.Result;
import com.sasu.takeout.entity.Category;
import com.sasu.takeout.entity.Dish;
import com.sasu.takeout.entity.Employee;
import com.sasu.takeout.entity.Setmeal;
import com.sasu.takeout.service.CategoryService;
import com.sasu.takeout.service.DishService;
import com.sasu.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("page")
    public Result<Page> categoryPage(int page,int pageSize){
        //构造分页器
        Page<Category> pageInfo = new Page(page,pageSize);

        //构造条件筛选器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //使用降序排序
        lambdaQueryWrapper.orderByDesc(Category::getSort);

        //查询信息
        categoryService.page(pageInfo,lambdaQueryWrapper);

        return Result.success(pageInfo);
    }

    @PostMapping
    public Result<String> addCategory(@RequestBody Category category){

        categoryService.save(category);

        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> updateCategory(@RequestBody Category category){
        categoryService.updateById(category);
        return Result.success("修改菜品成功");
    }

    @DeleteMapping
    public Result<String> deleteCategory(Long id){
        categoryService.remove(id);
        return Result.success("删除成功");

    }

    @GetMapping("/list")
    public Result<List<Category>> listResult(Category category){
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        lambdaQueryWrapper.orderByDesc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return Result.success(list);
    }


}
