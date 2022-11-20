package com.hg.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hg.reggie.entity.AddressBook;
import com.hg.reggie.mapper.AddressBookMapper;
import com.hg.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author HG
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
