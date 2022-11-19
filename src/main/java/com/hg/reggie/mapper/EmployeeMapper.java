package com.hg.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hg.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author HG
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
