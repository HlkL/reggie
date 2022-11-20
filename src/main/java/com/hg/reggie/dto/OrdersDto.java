package com.hg.reggie.dto;

import com.hg.reggie.entity.OrderDetail;
import com.hg.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author hougen
 * @program Reggie
 * @description
 * @create 2022-11-20 14:25
 */
@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;

}



