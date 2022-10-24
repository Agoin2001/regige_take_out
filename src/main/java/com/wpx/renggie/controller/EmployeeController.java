package com.wpx.renggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wpx.renggie.common.Result;
import com.wpx.renggie.entity.Employee;
import com.wpx.renggie.service.EmployeeService;
import com.wpx.renggie.service.impl.EmployeeServiceImpl;
import com.wpx.renggie.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;


    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request ,@RequestBody Employee employee){
        //1、将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据页面提交的用户名来查数据库
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());

        final Employee emp = employeeService.getOne(queryWrapper);


        //3、如果没有查询到则返回失败结果
        if (emp==null){
            return Result.error("登陆失败");
        }
        //4、比对密码，如果不一致则返回失败结果
        if (!emp.getPassword().equals(password)){
            return Result.error("密码错误!!!");
        }
        //5、查看员工状态，如果已禁用状态，则返回员工已禁用结果
        if (emp.getStatus()==0){
            return Result.error("账号已禁用!!!");
        }

        //6、登录成功，将用户id存入Session并返回成功结果
        request.getSession().setAttribute("employee",emp.getId());


        return Result.success(emp);


    }

    /**
     * 员工退出功能
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request ){
        //清除session中保留的当前登陆用户的id
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }


    /**
     * 保存用户信息
     */
    @PostMapping
    public Result<String> addEmployee(HttpServletRequest request,@RequestBody Employee employee){

        //设置初始密码，需要进行md5加密处理
        employee.setPassword(MD5Util.getMD5("123456"));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        final Long empID = (Long) request.getSession().getAttribute("employee");

        employee.setCreateUser(empID);
        employee.setUpdateUser(empID);

        employeeService.save(employee);

        return Result.success("插入成功");
    }


    /**
     * 分页展示员工列表接口、查询某个员工
     * @param page 查询第几页
     * @param pageSize 每页一共几条数据
     * @param name 查询名字=name的数据
     * @return 返回Page页
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize,String name){
        //分页构造器,Page(第几页, 查几条)
        Page pageInfo=new Page<>(page,pageSize);
        //查询构造器
        final LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件.like(什么条件下启用模糊查询，模糊查询字段，被模糊插叙的名称)
        queryWrapper.like(StringUtils.hasText(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        //返回查询结果
        return Result.success(pageInfo);

    }


    /**
     * 更新员工状态，是PUT请求
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PutMapping
    public Result<Employee> update(HttpServletRequest httpServletRequest,@RequestBody Employee employee){
        System.out.println("更新"+Thread.currentThread().getName());


        final long id = Thread.currentThread().getId();
        log.info("线程id为{}",id);

        //从Request作用域中拿到员工ID
        Long empId = (Long) httpServletRequest.getSession().getAttribute("employee");
        //拿新的状态值
        employee.setStatus(employee.getStatus());
        //更新时间
        employee.setUpdateTime(LocalDateTime.now());
        //更新处理人id
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);


        return Result.success(employee);
    }

    /**
     * 拿到员工资料，前端自动填充列表，更新的时候复用上面的update方法
     * @param id ResultFul风格传入参数，用@PathVariable来接收同名参数
     * @return 返回员工对象
     */
    @GetMapping("/{id}")
    public Result<Employee> getEmployee(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return Result.success(employee);
        }
        return Result.error("没有查到员工信息");
    }

}
