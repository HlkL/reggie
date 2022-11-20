package com.hg.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hg.reggie.common.R;
import com.hg.reggie.dto.OrdersDto;
import com.hg.reggie.entity.Orders;
import com.hg.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author hougen
 * @program Reggie
 * @description 订单支付
 * @create 2022-11-17 20:59
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单支付...");
        orderService.submit(orders);
        return R.success("支付成功");
    }

    /**
     * 订单明细
     * @param page
     * @param pageSize
     * @param number 订单号
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(Integer page,Integer pageSize,Long number,
                                String beginTime,String endTime){
        log.info("订单页面:{},页面大小:{},订单号:{},开始时间:{},结束时间:{}...",page,pageSize,number,beginTime,endTime);
        Page<Orders> ordersPage = orderService.page(page, pageSize, number, beginTime, endTime);
        return R.success(ordersPage);
    }


    /**
     * 修改订单状态
     * 订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> setStatus(@RequestBody Orders orders){
        log.info("订单状态:{}",orders.getStatus());
        orderService.setStatus(orders);
        return R.success("设置成功");
    }

    /**
     *  用户订单详情
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userPage(Integer page, Integer pageSize){
        log.info("用户订单详情...");
        Page<OrdersDto> userPage = orderService.userPage(page, pageSize);
        return R.success(userPage);
    }

    /**
     *  再来一单
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        log.info("再次订购{}",orders.getId());
        orderService.again(orders);
        return R.success("订购成功");
    }

}


