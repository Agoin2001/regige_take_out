package com.wpx.renggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wpx.renggie.entity.Category;

public interface CategoryService extends IService<Category>{

         void remove(Long[] id);


}
