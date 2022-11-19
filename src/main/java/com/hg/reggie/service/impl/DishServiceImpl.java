package com.hg.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.reggie.common.CustomException;
import com.hg.reggie.common.R;
import com.hg.reggie.dto.DishDto;
import com.hg.reggie.entity.Category;
import com.hg.reggie.entity.Dish;
import com.hg.reggie.entity.DishFlavor;
import com.hg.reggie.mapper.DishMapper;
import com.hg.reggie.service.CategoryService;
import com.hg.reggie.service.DishFlavorService;
import com.hg.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  EnableTransactionManagement 开启事务支持
 * @author hougen
 * @program Reggie
 * @description 菜品
 * @create 2022-11-14 15:09
 */
@Service
@Slf4j
@EnableTransactionManagement
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    /**
     * 菜品图片文件路径
     */
    @Value("${reggie.path}")
    private String filePath;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜品,保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFlavor(DishDto dishDto) {
        //将菜品的基本信息保存到菜品表
        this.save(dishDto);
        //菜品id
        Long dishId = dishDto.getId();

        //获取口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();

        //将菜品id加入口味数据表中
        flavors = flavors.stream().peek(item -> item.setDishId(dishId) ).collect(Collectors.toList());

        //保存菜品口味到口味表
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 菜品管理,分页界面
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<DishDto> pageList(Integer page, Integer pageSize, String name) {
        //分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name)
                .orderByDesc(Dish::getUpdateTime);


//        dishService.page(pageInfo,queryWrapper);
        this.page(pageInfo,queryWrapper);
        //拷贝数据
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map( item ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            //获取分类id
            Long categoryId = dishDto.getCategoryId();
            //通过分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if ( category != null ){
                //获取菜品分类名称
                dishDto.setCategoryName( category.getName() );
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return dishDtoPage;
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //根据id查询查询口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    /**
     * 修改菜品信息
     * @param dishDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDishFlavo(DishDto dishDto) {
        //修改菜品基本信息
        this.updateById(dishDto);

        //根据菜品id清除当前菜品的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //获取提交过来的菜品信息
        List<DishFlavor> flavors = dishDto.getFlavors();

        //将当前菜品id添加到口味信息中
        flavors = flavors.stream().peek(item ->item.setDishId( dishDto.getId()) ).collect(Collectors.toList());

        //将新的口味信息加入数据库
        dishFlavorService.saveBatch(flavors);
    }


    /**
     * 获取菜品数据
     * @param dish
     * @return
     */
    @Override
    public List<DishDto> getList(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //根据分类id查询在售状态的菜品
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId())
                .eq(Dish::getStatus,1)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);

        List<Dish> list = this.list(queryWrapper);

        return list.stream().map( item ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if( category != null ){
                dishDto.setCategoryName(category.getName());
            }

            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String[] id) {

        //当前菜品状态
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.in(Dish::getId,  id).eq(Dish::getStatus,1);
        long count = this.count(dishWrapper);
        if( count > 0 ){
            throw new CustomException("当前菜品处于在售状态,不能删除");
        }
        //删除菜品的口味信息
        LambdaQueryWrapper<DishFlavor> flavorWrapper = new LambdaQueryWrapper<>();
        flavorWrapper.in(DishFlavor::getDishId,id);
        dishFlavorService.remove(flavorWrapper);
        log.info("删除菜品...");

        for (String s : id) {
            //查询菜品图片文件名
            Dish byId = this.getById(s);
            //获取图片路径
            File file = new File(filePath+byId.getImage());
            if( file.exists() ){
                file.delete();
            }else {
                log.info("图片资源删除失败...");
            }
            this.removeById(Long.parseLong(s));
        }
    }

    @Override
    public void selling(Integer flag, String ids) {
        String[] id = ids.split(",");
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        Dish dish = new Dish();
        if ( flag == 0 ){
            dish.setStatus(0);
            updateWrapper.in(Dish::getId,  id);
        }else {
            dish.setStatus(1);
            updateWrapper.in(Dish::getId,  id);
        }

        this.update(dish,updateWrapper);
    }

}


