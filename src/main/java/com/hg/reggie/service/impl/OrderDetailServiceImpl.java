package com.hg.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.reggie.entity.OrderDetail;
import com.hg.reggie.mapper.OrderDetailMapper;
import com.hg.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author HG
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}