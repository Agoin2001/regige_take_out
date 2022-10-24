package com.wpx.renggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wpx.renggie.entity.User;
import com.wpx.renggie.mapper.UserMapper;
import com.wpx.renggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author cc
 * @since 2022-05-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
