package com.hg.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hg.reggie.common.EmailUtil;
import com.hg.reggie.common.R;

import com.hg.reggie.entity.User;
import com.hg.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author hougen
 * @program Reggie
 * @description 前端用户登录
 * @create 2022-11-16 22:58
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private UserService userService;
    /**
     * 发送验证码
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        log.info("{}请求发送验证码...",user.getPhone());
        if( user.getPhone() != null ) {
            //获取6位随机验证码
            String code = emailUtil.generateVerificationCode();
            log.info(code);

            //发送验证码
//            emailUtil.sendSimpleMail(user.getPhone());

            //需要将生成的验证码保存到Session
            session.setAttribute(user.getPhone(), code);
            return R.success("验证码发送成功");
        }
        return R.error("发送失败");
    }

    /**
     * 用户登录
     * @param account
     * @return
     */
    @PostMapping("/login")
    public R<User> userLogin(@RequestBody Map account, HttpSession session){
        log.info(account.toString());
        //获取输入的账号和验证码
        String phone = account.get("phone").toString();
        String code = account.get("code").toString();

        //获取session中的验证码
        String s = session.getAttribute(phone).toString();

        if( s != null && s.equals(code) ){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> userLogOut(HttpSession session){
        log.info("用户退出登录...");
        session.removeAttribute("user");
        return R.success("用户退出登录");
    }
}
