package com.hg.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hg.reggie.entity.Employee;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HG
 */
public interface EmployeeService extends IService<Employee> {

    Employee login(Employee employee);

    Page<Employee> page(Integer page, Integer pageSize, String name);
}
