package com.hg.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hg.reggie.entity.ShoppingCart;

import java.util.List;

/**
 * @author HG
 */
public interface ShoppingCartService extends IService<ShoppingCart> {
    ShoppingCart add( ShoppingCart shoppingCart);

    /**
     * 获取当前的用户购物车中的订单信息
     */
    List<ShoppingCart> getList();

    /**
     * 清空当前用户购物车订单信息
     */
    boolean clear();

    ShoppingCart del(ShoppingCart shoppingCart);
}
