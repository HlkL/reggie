package com.hg.reggie.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hg.reggie.dto.OrdersDto;
import com.hg.reggie.entity.Orders;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author HG
 */
public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders);

    Page<Orders> page(Integer page, Integer pageSize, Long number, String beginTime, String endTime);

    void setStatus( Orders orders);

    Page<OrdersDto> userPage(Integer page, Integer pageSize);

    void again( Orders orders);
}
