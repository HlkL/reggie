package com.hg.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.reggie.common.R;
import com.hg.reggie.entity.Employee;
import com.hg.reggie.mapper.EmployeeMapper;
import com.hg.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hougen
 * @program Reggie
 * @description
 * @create 2022-11-13 02:23
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public Employee login( Employee employee) {
        //根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        //查询数据库中的数据
        return this.getOne(queryWrapper);
    }

    @Override
    public Page<Employee> page(Integer page, Integer pageSize, String name) {

        //分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name)
                .orderByDesc(Employee::getUpdateTime);
        this.page(pageInfo,queryWrapper);
        return pageInfo;
    }

}


