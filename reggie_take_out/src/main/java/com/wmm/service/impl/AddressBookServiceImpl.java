package com.wmm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmm.entity.AddressBook;
import com.wmm.mapper.AddressBookMapper;
import com.wmm.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
