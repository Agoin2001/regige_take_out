package com.wpx.renggie.controller;


import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wpx.renggie.common.Result;
import com.wpx.renggie.entity.User;
import com.wpx.renggie.service.UserService;
import com.wpx.renggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author cc
 * @since 2022-05-30
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

@Resource
private UserService userService;

@Resource
private RedisTemplate redisTemplate;



    /**
     * 验证码发送
     * @param user 接收用户电话号码
     * @param session 把验证码存入session，后续登陆验证要用
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone=user.getPhone();
        if(!StringUtils.isEmpty(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //需要将生成的验证码保存到Session
            //session.setAttribute(phone,code);

            //将验证码缓存到reids中，设置时间为5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return Result.success("手机验证码短信发送成功");
        }
        return Result.error("手机短信发送失败");
    }


    @PostMapping("/login")
    public Result<String> login(@RequestBody Map map, HttpSession session){

        //获取手机号
        final String phone = map.get("phone").toString();

        //获取验证码
        final String code = map.get("code").toString();

        //final Object codeInsession = session.getAttribute(phone);

        //从redis中获取保存的验证码
        final String codeInsession = (String) redisTemplate.opsForValue().get(phone);

        if (codeInsession!=null && code.equals(codeInsession)){
            //如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);

            if (user==null){
                //判断当前手机号是否为新用户，如果是新用户则自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
           session.setAttribute("user",user.getId());

//            //此时已经登陆成功，向Redis中存入userId的信息留给过滤器进行验证放行
//            redisTemplate.opsForValue().set("user", user.getId());


            //如果用户登录成功则删除Redis中缓存的验证码
            redisTemplate.delete(phone);

            return Result.success("登陆成功，欢迎~");
        }

        return Result.error("短信发送失败");
    }




}
