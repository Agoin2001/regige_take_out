package com.wpx.renggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wpx.renggie.common.Result;
import com.wpx.renggie.dto.DishDto;
import com.wpx.renggie.entity.Category;
import com.wpx.renggie.entity.Dish;
import com.wpx.renggie.entity.DishFlavor;
import com.wpx.renggie.service.CategoryService;
import com.wpx.renggie.service.DishFlavorService;
import com.wpx.renggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private RedisTemplate redisTemplate;



    /**
     * 新增菜品
     * @param dishDto 传输对象
     * @return
     */
    @PostMapping()
    public Result<String> addDish(@RequestBody DishDto dishDto) {

        dishService.addDishWithFlavor(dishDto);

        //清理某个分类下面的菜品缓存数据
        String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);

        return Result.success("新增菜品成功");
    }




    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name){

        //构造分页构造器
        final Page<Dish> pageInfo = new Page<>(page, pageSize);
        //DishDto是前端要的东西和后端的Dish不一样，要扩展一下
        Page<DishDto> dishDtoPage = new Page<>();


        //条件过滤器
        final LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝，忽略record对象，因为record就是查出来的记录数，也就是pageInfo
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        //将List集合搬入Dto中
        //这里是流式编程的内容，或者用foreach来进行搬运也可以解决
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return Result.success(dishDtoPage);
    }


    /**
     * 修改菜品信息的回显功能，填充到修改页面为修改菜品做准备
     * @param id 菜品id
     * @return
     */
    //@CachePut(value = "userCache",key="#dishDto.id")
    @GetMapping("/{id}")
    public Result<DishDto> updateDish(@PathVariable Long id){
        //因为是直接查Dto数据嘛，用现成的肯定不行了，在Service层自己写，这是个多表联查的过程
        DishDto dishDto=dishService.getByIdWithFlavor(id);

        return Result.success(dishDto);
    }


    //修改菜品
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

//        //清理所有菜品缓存数据
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);

        //清理某个分类下面的菜品缓存数据
        String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);

        return Result.success("修改菜品成功");
    }


    /**
     * 新增套餐时 根据条件查询并填充菜品列表
     * @param dish 这里本来是categoryId的，但是为了保证通用性，这里用Dish对象进行封装 有更好的通用性
     *             Dish本身里面也是有categoryId的
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishDto>> listCategory(Dish dish){

        //结果返回对象
        List<DishDto> dishDtoList = null;

        //缓存优化
        //构造一个存入redis的key值
        String redisKey = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //从Redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(redisKey);
        //如果有，到这里就直接返回结束了
        if (dishDtoList != null) {
            return Result.success(dishDtoList);
        }
        //如果没有，就根据查询数据库，再根据构造的Key存入一个菜品数据
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询
        lambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //只查在售状态的菜品，1为启售状态
        lambdaQueryWrapper.eq(Dish::getStatus, 1);
        //排序，多个字段排序，先按Sort排，再按UpdateTime排
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(lambdaQueryWrapper);


         dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            //根据id查分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, dishId);
            //SQL: select* from dishflavor where dish_id=?;
            List<DishFlavor> dishFlavorlist = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(dishFlavorlist);
            return dishDto;
        }).collect(Collectors.toList());

        //把从数据库里查出来的数据进行缓存，设置好过期时间60s
        redisTemplate.opsForValue().set(redisKey,dishDtoList,60, TimeUnit.MINUTES);
        return Result.success(dishDtoList);

    }

}
