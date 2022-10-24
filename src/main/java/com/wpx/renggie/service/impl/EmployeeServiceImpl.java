package com.wpx.renggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wpx.renggie.entity.Employee;
import com.wpx.renggie.mapper.EmployeeMapper;
import com.wpx.renggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}