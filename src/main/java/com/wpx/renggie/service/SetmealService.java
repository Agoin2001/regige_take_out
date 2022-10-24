package com.wpx.renggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wpx.renggie.dto.SetmealDto;
import com.wpx.renggie.entity.Setmeal;

import java.util.List;

/**
 * <p>
 * 套餐 服务类
 * </p>
 *
 * @author cc
 * @since 2022-05-30
 */
public interface SetmealService extends IService<Setmeal> {

    //新增套餐，同时要保持与菜品的关联关系
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    SetmealDto getSetmealData(Long id);

    void updateWithDish(SetmealDto setmealDto);

}
