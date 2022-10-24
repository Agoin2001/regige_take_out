package com.wpx.renggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wpx.renggie.entity.Orders;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author cc
 * @since 2022-05-30
 */
public interface OrdersService extends IService<Orders> {

    public void submit(Orders orders);
}
