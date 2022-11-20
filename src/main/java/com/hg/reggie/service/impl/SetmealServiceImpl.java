package com.hg.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.reggie.common.CustomException;
import com.hg.reggie.common.R;
import com.hg.reggie.dto.SetmealDto;
import com.hg.reggie.entity.Category;
import com.hg.reggie.entity.Setmeal;
import com.hg.reggie.entity.SetmealDish;
import com.hg.reggie.mapper.SetmealMapper;
import com.hg.reggie.service.CategoryService;
import com.hg.reggie.service.SetmealDishService;
import com.hg.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hougen
 * @program Reggie
 * @description 套餐
 * @create 2022-11-14 15:13
 */
@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 保存套餐信息
     * @param setmealDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithDish(SetmealDto setmealDto) {
        //保存菜品基本数据
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes =setmealDishes.stream().peek(item -> item.setSetmealId(setmealDto.getId())).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 套餐分页列表
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<SetmealDto> pageList(Integer page, Integer pageSize, String name) {

        Page<Setmeal> setmealPage = new Page<>(page,pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name)
                .orderByDesc(Setmeal::getUpdateTime);

        this.page(setmealPage,queryWrapper);

        Page<SetmealDto> setmealDtoPage = new Page<>();

        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> list = records.stream().map( item ->{
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item,setmealDto);
            Category category = categoryService.getById(item.getCategoryId());

            if( category != null ){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);
        return setmealDtoPage;
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemove(List<Long> ids) {
        //查看当前套餐状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids)
                .eq(Setmeal::getStatus,1);

        long count = this.count(queryWrapper);
        if( count > 0 ){
            throw new CustomException("当前套餐在启售状态,不能删除");
        }

        //删除套餐信息
        this.removeBatchByIds(ids);

        //删除套餐中的菜品信息
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(dishLambdaQueryWrapper);
    }

    @Override
    public List<Setmeal> getList(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId())
                .eq( setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());

        return this.list(queryWrapper);
    }

    @Override
    public void selling(Integer flag, String id) {
        String[] ids = id.split(",");
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        Setmeal setmeal = new Setmeal();
        if( flag == 0 ){
            setmeal.setStatus(0);
            updateWrapper.in(Setmeal::getId,ids);
        }else {
            setmeal.setStatus(1);
            updateWrapper.in(Setmeal::getId,ids);
        }
        this.update(setmeal,updateWrapper);
    }

    @Override
    public SetmealDto get(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = this.getById(id);

        BeanUtils.copyProperties(setmeal,setmealDto);

        Long setmealId = setmeal.getId();

        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> dishList = setmealDishService.list(wrapper);

        setmealDto.setSetmealDishes(dishList);
        return setmealDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSetMeal(SetmealDto setmealDto) {
        //删除订单的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //删除订单信息
        this.removeById(setmealDto.getId());

        //重置套餐id
        setmealDto.setId(null);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach( item -> {
            item.setSetmealId(null);
        });

        //保存新的订单信息
        this.saveWithDish(setmealDto);
    }


}


