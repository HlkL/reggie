package com.hg.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hg.reggie.common.R;
import com.hg.reggie.dto.DishDto;
import com.hg.reggie.entity.Dish;
import com.hg.reggie.entity.DishFlavor;
import com.hg.reggie.service.DishFlavorService;
import com.hg.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

/**
 * @author hougen
 * @program Reggie
 * @description 菜品管理
 * @create 2022-11-14 23:30
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page,Integer pageSize,String name){
        log.info("当前菜品页面:{},页面大小:{},菜品名称:{}",page,pageSize,name);

        Page<DishDto> dishDtoPage = dishService.pageList(page, pageSize, name);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息响应到前端
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getByIdWithFlavor(@PathVariable Long id){
        log.info("getByIdWithFlavor方法:[{}]...",id);
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 修改菜品口味信息
     * @param dishDto
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateDishFlavo(dishDto);
        return R.success("修改成功");
    }

    /**
     * 获取每个菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        log.info("获取菜品...");
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        //根据分类id查询在售状态的菜品
//        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId())
//                .eq(Dish::getStatus,1)
//                .orderByAsc(Dish::getSort)
//                .orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        log.info("获取菜品...");
        return R.success(dishService.getList(dish));
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(String ids){
        log.info(ids);
        String[] id = ids.split(",");
        //获取多个id
        dishService.delete(id);
        return R.success("删除成功");
    }



    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, String ids){
        log.info("设置菜品售卖状态...");
        log.info("设置{}菜品,状态{}...",ids,status);
        dishService.selling(status, ids);
        return R.success("设置成功");
    }
}


