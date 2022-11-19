package com.hg.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hg.reggie.dto.DishDto;
import com.hg.reggie.entity.Dish;

import java.util.List;


/**
 * @author HG
 */
public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    Page<DishDto> pageList(Integer page, Integer pageSize, String name);

    DishDto getByIdWithFlavor(Long id);

    void updateDishFlavo(DishDto dishDto);

    List<DishDto> getList(Dish dish);

    void delete(String[] id);

    void selling(Integer flag,String ids);
}
