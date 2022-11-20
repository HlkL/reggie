package com.hg.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.reggie.entity.User;
import com.hg.reggie.mapper.UserMapper;
import com.hg.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author HG
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
