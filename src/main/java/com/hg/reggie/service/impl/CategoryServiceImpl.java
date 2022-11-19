package com.hg.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.reggie.common.CustomException;
import com.hg.reggie.entity.Category;
import com.hg.reggie.entity.Dish;
import com.hg.reggie.entity.Setmeal;
import com.hg.reggie.mapper.CategoryMapper;
import com.hg.reggie.service.CategoryService;
import com.hg.reggie.service.DishService;
import com.hg.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分类
 * @author HG
 */
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    /**
     * 菜品
     */
    @Autowired
    private DishService dishService;

    /**
     * 套餐
     */
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        //查询当前分类关联的菜品
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId,id);
        if (dishService.count(dishWrapper) > 0) {
            throw new CustomException("当前分类下有关联菜品,不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId,id);
        if ( setmealService.count(setmealWrapper) > 0 ){
            throw new CustomException("当前分类下有关联套餐,不能删除");
        }

        super.removeById(id);
    }
}