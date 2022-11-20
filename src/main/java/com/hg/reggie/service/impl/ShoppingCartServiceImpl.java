package com.hg.reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.reggie.common.BaseContext;
import com.hg.reggie.common.CustomException;
import com.hg.reggie.entity.ShoppingCart;
import com.hg.reggie.mapper.ShoppingCartMapper;
import com.hg.reggie.service.DishService;
import com.hg.reggie.service.SetmealService;
import com.hg.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author HG
 */
@Slf4j
@Service
@EnableTransactionManagement
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 添加订单
     */
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();

        //获取当前登录用户的id
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if( dishId != null ){
            //菜品订单
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //查询当前订单是否已经有加入过购物车
        ShoppingCart cart = this.getOne(queryWrapper);

        if( cart != null ){
            //将当前订单数量加1
            cart.setNumber(cart.getNumber()+1);
            this.updateById(cart);
        }else {
            //将当前订单加入购物车
            shoppingCart.setNumber(1);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            cart = shoppingCart;
        }
        return cart;
    }

    /**
     * 获取当前用户购物车订单信息
     * @return
     */
    @Override
    public List<ShoppingCart> getList(){
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        return this.list(queryWrapper);
    }

    /**
     * 清空当前用户购物车订单信息
     * @return
     */
    @Override
    public boolean clear() {
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        return this.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShoppingCart del(ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        //删除登录用户购物信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        if( dishId != null ){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }
        ShoppingCart one = this.getOne(queryWrapper);
        if( one == null ){
            throw new CustomException("空购物车,不能删除");
        }else if ( one.getNumber() > 1 ){
            //数量减1
            one.setNumber(one.getNumber() - 1);
            this.updateById(one);
        }else if ( one.getNumber() == 1 ){
            this.remove(queryWrapper);
        }else{
            throw new CustomException("删除失败");
        }
        return one;
    }

}
