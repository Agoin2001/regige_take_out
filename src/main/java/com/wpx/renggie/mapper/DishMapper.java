package com.wpx.renggie.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wpx.renggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;


/**
 * <p>
 * 菜品管理 Mapper 接口
 * </p>
 *
 * @author cc
 * @since 2022-05-30
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}
