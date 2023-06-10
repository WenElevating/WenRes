package com.wmm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmm.entity.User;
import com.wmm.mapper.UserMapper;
import com.wmm.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
