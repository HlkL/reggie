package com.hg.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hg.reggie.common.R;
import com.hg.reggie.entity.Category;
import com.hg.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author hougen
 * @program Reggie
 * @description 菜品
 * @create 2022-11-14 13:35
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /**
     * 添加菜品及套餐分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info(category.toString());
        categoryService.save(category);
        return R.success("添加成功");
    }

    /**
     * 分页展示
     */
    @GetMapping("/page")
    public R<Page<Category>> page(Integer page,Integer pageSize){
        log.info("当前页面编号:{},页面大小:{}",page,pageSize);
        Page<Category> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除
     */
    @DeleteMapping
    public R<String> remove(Long id){
        log.info("分类删除,[id={}]...",id);
        categoryService.remove(id);
        return R.success("删除成功");
    }

    /**
     * 修改分类信息
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息,[id={}]...",category.getId());
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 菜品列表
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        log.info("获取菜品列表...");

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() !=null,Category::getType,category.getType())
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}


