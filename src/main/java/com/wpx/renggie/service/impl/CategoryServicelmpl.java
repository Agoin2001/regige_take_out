package com.wpx.renggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wpx.renggie.common.CustomerException;
import com.wpx.renggie.entity.Category;
import com.wpx.renggie.entity.Dish;
import com.wpx.renggie.entity.Setmeal;
import com.wpx.renggie.mapper.CategoryMapper;
import com.wpx.renggie.service.CategoryService;
import com.wpx.renggie.service.DishService;
import com.wpx.renggie.service.SetmealService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;

@Service
public class CategoryServicelmpl  extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private DishService dishService;

    @Resource
    private SetmealService setmealService;


    /**
     * 根据id删除分类
     * @param ids
     */
    @Override
    public void remove(Long[] ids) {
//        //查询当前菜品分类下是否还有菜品，
//        final LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        //添加查询条件
//        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
//
//        final int dishcount = dishService.count(dishLambdaQueryWrapper);
//        //如果有菜品就不能删除，抛出异常打断
//        if (dishcount > 0){
//            //已经与菜品关联了，抛异常，不许删
//            throw new CustomerException("已经与菜品关联了，请删除完菜品再来删除");
//        }
//
//
//        //查询当前菜品分类是否关联了套餐，如果有套餐就不能删除，抛出异常打断
//        final LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        //添加查询条件
//        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
//
//        final int setmealcount = setmealService.count(setmealLambdaQueryWrapper);
//        //如果有菜品就不能删除，抛出异常打断
//        if (setmealcount > 0){
//            //已经与套餐关联了，抛异常，不许删
//            throw new CustomerException("已经与菜品关联了，请删除完菜品再来删除");
//        }
//
//
//        //正常删除分类
//        super.removeById(id);

        //查询当前菜品分类下是否还有菜品，如果有菜品就不能删除，抛出异常打断
        LambdaQueryWrapper<Dish> dishLambdaQueryWarpper = new LambdaQueryWrapper();
        //看看所有的菜品下有没有目标分类与之关联
        dishLambdaQueryWarpper.eq(Dish::getCategoryId, ids);
        int countInDishById=dishService.count(dishLambdaQueryWarpper);
        if (countInDishById>0){
            //已经与菜品关联了，抛异常，不许删
            throw new CustomerException("已经与菜品关联了，请删除完菜品再来删除");
        }
        //查询当前菜品分类是否关联了套餐，如果有套餐就不能删除，抛出异常打断
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWarpper = new LambdaQueryWrapper();
        setmealLambdaQueryWarpper.eq(Setmeal::getCategoryId, ids);
        //看看所有的套餐下有没有目标分类与之关联
        int countInSetmealById = setmealService.count(setmealLambdaQueryWarpper);
        if (countInSetmealById>0){
            //已经与套餐关联了，抛异常，不许删
            throw new CustomerException("已经与套餐关联了，请删除完套餐再来删除");
        }
        //上面的测试都通过了，没有任何阻碍了，允许删除，直接调用父接口继承的MP的删除方法就行
        super.removeByIds(Arrays.asList(ids));
    }




}
