package com.hg.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hg.reggie.common.R;
import com.hg.reggie.dto.SetmealDto;
import com.hg.reggie.entity.Setmeal;
import com.hg.reggie.service.SetmealDishService;
import com.hg.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author hougen
 * @program Reggie
 * @description 套餐
 * @create 2022-11-16 12:22
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 添加套餐信息
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@NotNull @RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }

    /**
     * 套餐分页展示
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name){
        log.info("当前套餐页面:{},页面大小{},套餐名称:{}",page,pageSize,name);

        Page<SetmealDto> setmealDtoPage = setmealService.pageList(page, pageSize, name);

        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> remove(@RequestParam List<Long> ids){
        log.info("删除{}套餐信息...",ids);
        setmealService.batchRemove(ids);
        return R.success("删除成功");
    }

    /**
     * 获取套餐列表
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> getList(Setmeal setmeal){
        log.info("获取套餐信息{}...",setmeal);
        return R.success(setmealService.getList(setmeal));
    }

    /**
     * 设置套餐状态
     * @param flag
     * @param ids
     * @return
     */
    @PostMapping("/status/{flag}")
    public R<String> status(@PathVariable Integer flag,String ids){
        log.info("设置套餐状态...");
        setmealService.selling(flag,ids);
        return R.success("设置成功");
    }

    /**
     * 获取套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        log.info("获取套餐数据...");
        SetmealDto setmealDto = setmealService.get(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("修改套餐信息...");
        setmealService.updateSetMeal(setmealDto);
        return R.success("修改成功");
    }

}


