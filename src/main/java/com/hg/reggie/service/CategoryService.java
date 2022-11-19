package com.hg.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hg.reggie.entity.Category;

/**
 * @author HG
 */
public interface CategoryService extends IService<Category> {

    /**
     * 删除套餐
     * @param id
     */
    void remove(Long id);
}
