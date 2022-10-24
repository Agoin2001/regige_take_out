package com.wpx.renggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wpx.renggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
