package com.hg.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hg.reggie.common.R;
import com.hg.reggie.entity.Employee;
import com.hg.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hougen
 * @program Reggie
 * @description
 * @create 2022-11-13 02:26
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //查询数据库中的数据
        Employee emp = employeeService.login(employee);

        //是否存在数据
        if (emp == null) {
            return R.error("登录失败");
        }

        //密码比对
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        //员工账号状态
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //登录成功,将员工id存入session中
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<Employee> logout(HttpServletRequest request) {
        //清除session中的员工信息
        request.removeAttribute("employee");
        return R.success("退出成功");
    }


    /**
     * 添加用户
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee) {
        log.info("新增员工,员工信息: {}", employee.toString());
        //初始化密码,公共字段自动填充
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setStatus(1);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long id = (Long)request.getSession().getAttribute("employee");
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);
        employeeService.save(employee);
        return R.success("员工添加成功");
    }

    /**
     * 分页
     * Request URL: http://localhost:8080/employee/page?page=1&pageSize=20&name=asd
     */
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name){
        log.info("page={},pages={},name={}",page,pageSize,name);
        return R.success(employeeService.page(page,pageSize,name));
    }

    /**
     * 修改员工账号信息
     * js处理Long类型会丢失精度
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        //只有管理元才能修改员工信息,且管理员信息不能被修改
        if( (Long)request.getSession().getAttribute("employee") == 1L && employee.getId() != 1 ){

//            employee.setUpdateUser( (Long) request.getSession().getAttribute("employee"));
//            employee.setUpdateTime(LocalDateTime.now());

            employeeService.updateById(employee);
            return R.success("员工信息修改成功");
        }

        return R.error("修改失败");
    }

    /**
     * 获取员工信息
     */
    @GetMapping("/{id}")
    public R<Employee> getUserById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if( employee != null ){
            return R.success(employee);
        }
        return R.error("获取信息失败");
    }
}


