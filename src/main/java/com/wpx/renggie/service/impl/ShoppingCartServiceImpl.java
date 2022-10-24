package com.wpx.renggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wpx.renggie.entity.ShoppingCart;
import com.wpx.renggie.mapper.ShoppingCartMapper;
import com.wpx.renggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-05-30
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
