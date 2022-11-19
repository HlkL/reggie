package com.hg.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hg.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author HG
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
