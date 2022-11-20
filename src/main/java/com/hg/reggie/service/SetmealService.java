package com.hg.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hg.reggie.dto.SetmealDto;
import com.hg.reggie.entity.Setmeal;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author HG
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 保存套餐信息
     * @param setmealDto
     * @return
     */
    void saveWithDish(SetmealDto setmealDto);

    Page<SetmealDto> pageList(Integer page,Integer pageSize,String name);

    void batchRemove(List<Long> ids);


    List<Setmeal> getList(Setmeal setmeal);

    void selling(Integer flag, String id);

    SetmealDto get(Long id);

    void updateSetMeal( SetmealDto setmealDto);
}
