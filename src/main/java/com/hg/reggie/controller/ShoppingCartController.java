package com.hg.reggie.controller;

import com.hg.reggie.common.R;
import com.hg.reggie.entity.ShoppingCart;
import com.hg.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author hougen
 * @program Reggie
 * @description
 * @create 2022-11-17 15:48
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("添加{}订单到购物车...",shoppingCart.getName());
        return R.success(shoppingCartService.add(shoppingCart));
    }

    @GetMapping("list")
    public R<List<ShoppingCart>> getList(){
        log.info("查看购物车订单...");
        return R.success(shoppingCartService.getList());
    }

    @DeleteMapping("/clean")
    public R<String> clear(){
        log.info("清空购物车订单...");
        if( shoppingCartService.clear() ){
            return R.success("清空成功");
        }
        return R.error("失败");
    }

    /**
     * 删除购物车数据
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> delete(@RequestBody ShoppingCart shoppingCart){
        log.info("删除购物车菜品&套餐...");
        ShoppingCart shoppingCartNew = shoppingCartService.del(shoppingCart);
        return R.success(shoppingCartNew);
    }
}


