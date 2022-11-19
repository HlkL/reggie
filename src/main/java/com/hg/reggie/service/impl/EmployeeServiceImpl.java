package com.hg.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.reggie.entity.Employee;
import com.hg.reggie.mapper.EmployeeMapper;
import com.hg.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author hougen
 * @program Reggie
 * @description
 * @create 2022-11-13 02:23
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}


