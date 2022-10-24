package com.wpx.renggie.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.wpx.renggie.dto.DishDto;
import com.wpx.renggie.entity.Dish;

/**
 * <p>
 * 菜品管理 服务类
 * </p>
 *
 * @author cc
 * @since 2022-05-30
 */
public interface DishService extends IService<Dish> {

    public void addDishWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
