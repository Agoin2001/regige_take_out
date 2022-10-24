package com.wpx.renggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wpx.renggie.entity.DishFlavor;
import com.wpx.renggie.mapper.DishFlavorMapper;
import com.wpx.renggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl  extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
