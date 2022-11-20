package com.hg.reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.reggie.common.BaseContext;
import com.hg.reggie.common.CustomException;
import com.hg.reggie.dto.OrdersDto;
import com.hg.reggie.entity.*;
import com.hg.reggie.mapper.OrderMapper;
import com.hg.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author HG
 */
@Service
@Slf4j
@EnableTransactionManagement
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Transactional(rollbackFor = Exception.class)
    public void submit(Orders orders) {
        //获得当前用户id
        Long userId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);

        if(shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空，不能下单");
        }

        //查询用户数据
        User user = userService.getById(userId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomException("用户地址信息有误，不能下单");
        }

        //订单号
        long orderId = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        //总金额
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(wrapper);
    }

    /**
     * 订单详细
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    public Page<Orders> page(Integer page, Integer pageSize, Long number, String beginTime, String endTime){
        LocalDateTime localBeginTime=null;
        LocalDateTime localEndTime=null;

        if( beginTime != null && endTime != null ){
            //时间转换格式
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            localBeginTime = LocalDateTime.parse(beginTime,df);
            localEndTime = LocalDateTime.parse(endTime,df);
        }
        //查询指定时间段订单信息
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(number!= null,Orders::getNumber,number)
                .between(localBeginTime != null && localEndTime != null,
                        Orders::getOrderTime,localBeginTime,localEndTime);

        Page<Orders> ordersPage = new Page<>(page, pageSize);
        this.page(ordersPage, queryWrapper);

        List<Orders> records = ordersPage.getRecords();
        records.forEach( item -> item.setUserName(item.getConsignee()));

        return ordersPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setStatus(Orders orders) {
        this.updateById(orders);
    }

    /**
     * 订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<OrdersDto> userPage(Integer page, Integer pageSize) {

        Page<Orders> ordersPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId())
                .orderByDesc(Orders::getOrderTime);
        this.page(ordersPage, queryWrapper);

        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");

        ordersDtoPage.setRecords(ordersPage.getRecords().stream().map( itme -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(itme,ordersDto);
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId,itme.getNumber());
            List<OrderDetail> list = orderDetailService.list(wrapper);
            if( list != null ){
                ordersDto.setOrderDetails(list);
            }
            return ordersDto;
        }).collect(Collectors.toList()));
        return ordersDtoPage;
    }

    @Override
    public void again(Orders orders) {
        //获取订单id
        Long id = orders.getId();
        //获取旧订单基本信息
        Orders old = this.getById(id);
        //查询购物菜品&套餐数据
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderId,old.getNumber());
        List<OrderDetail> orderDetails = orderDetailService.list(wrapper);

        //创建购物车对象,用于存放订单数据
        ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();

        //购物数据分类加入购物车
        for (OrderDetail orderDetail : orderDetails) {
            ShoppingCart newShoppingCart = new ShoppingCart();
            newShoppingCart.setUserId(BaseContext.getCurrentId());
            //菜品id
            Long dishId = orderDetail.getDishId();
            if( dishId != null ){
                //菜品数据
                newShoppingCart.setNumber(orderDetail.getNumber());
                newShoppingCart.setAmount(orderDetail.getAmount());
                newShoppingCart.setDishFlavor(orderDetail.getDishFlavor());
                newShoppingCart.setDishId(orderDetail.getDishId());
                newShoppingCart.setImage(orderDetail.getImage());
                newShoppingCart.setName(orderDetail.getName());
            }else {
                //套餐数据
                newShoppingCart.setNumber(orderDetail.getNumber());
                newShoppingCart.setAmount(orderDetail.getAmount());
                newShoppingCart.setDishFlavor(orderDetail.getDishFlavor());
                newShoppingCart.setSetmealId(orderDetail.getSetmealId());
                newShoppingCart.setImage(orderDetail.getImage());
                newShoppingCart.setName(orderDetail.getName());
            }
            newShoppingCart.setCreateTime(LocalDateTime.now());
            //将订单数据加入购物车
            shoppingCarts.add(newShoppingCart);
        }
        shoppingCartService.saveBatch(shoppingCarts);
    }
}