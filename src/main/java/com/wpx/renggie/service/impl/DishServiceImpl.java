package com.wpx.renggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wpx.renggie.dto.DishDto;
import com.wpx.renggie.entity.Dish;
import com.wpx.renggie.entity.DishFlavor;
import com.wpx.renggie.mapper.DishMapper;
import com.wpx.renggie.service.DishFlavorService;
import com.wpx.renggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 菜品管理 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-05-30
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Resource
    private DishService dishService;

    @Resource
    private DishFlavorService dishFlavorService;

    @Transactional
    @Override
    public void addDishWithFlavor(DishDto dishDto) {

        //因为DishDto是包含了Dish的信息，所以可以先存Dish信息到Dish表中，DishDto扩展的数据可以下一步再存
        //为什么这里传dishDto可以，因为DishDto是Dish的子类
        dishService.save(dishDto);

        //拿ID和口味List，为存DishDto做准备
        Long dishId = dishDto.getId();
        List<DishFlavor> flavor = dishDto.getFlavors();
        //遍历
        for (DishFlavor dishFlavors:flavor) {
            dishFlavors.setDishId(dishId);
        }

        //saveBatch是批量集合的存储
        dishFlavorService.saveBatch(flavor);

    }



    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {

        //先把普通信息查出来
        final Dish dish = this.getById(id);
        final DishDto dishDto = new DishDto();
        //搬运
        BeanUtils.copyProperties(dish,dishDto);
        //在通过dish的分类信息查口味List
        final LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        final List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);


        return dishDto;
    }


    /**
     * 更新口味操作，和上面的添加操作异曲同工
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //Dish表是可以直接更新操作的,这里也是一样的，传入的是Dish的子类，可以直接操作，默认也就是按Dish类更新了
        dishService.updateById(dishDto);

        //Dish_Flavor表比较特殊，所以需要先删除再插入
        //Dish_Flavor表字段删除，所有当前dish id的口味
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper();
        //子类可以直接获取父类的内容了
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);

        //再插入
        List<DishFlavor> flavorList=dishDto.getFlavors();
        //遍历
        for (DishFlavor dishFlavors:flavorList) {
            dishFlavors.setDishId(dishDto.getId());
        }
        //saveBatch是批量集合的存储
        dishFlavorService.saveBatch(flavorList);

    }
}
