package com.wmm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wmm.common.CustomException;
import com.wmm.common.R;
import com.wmm.dto.UserDto;
import com.wmm.entity.User;
import com.wmm.service.UserService;
import com.wmm.utils.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailUtil emailUtil;

    /**
     * 发送邮箱验证码
     * @param user
     * @return
     */
    @PostMapping("/emailCode")
    public R<String> sendEmailVerifyCode(@RequestBody User user){
        log.info("email = {}",user);

        // 发送邮箱

        boolean sendStatus = emailUtil.sendEmail(user.getEmail());

        if(!sendStatus){
            throw new CustomException("验证码发送失败！请联系管理员");
        }

        return R.success("验证码发送成功");
    }

    @PostMapping("/login")
    public R<String> userLogin(@RequestBody UserDto dto, HttpServletRequest request){
        // 数据格式校验

        log.info("dto = {}",dto);

        // 检查验证码是否正确
        boolean verifyResult = emailUtil.emailVerify(dto.getEmail(), dto.getCode());

        if(!verifyResult){
            return R.error("验证码错误");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(User::getEmail,dto.getEmail());

        User existUser = userService.getOne(wrapper);

        // 查询不存在，自动注册
        if(existUser == null){

            User user = new User();

            user.setEmail(dto.getEmail());

            user.setStatus(1);

            userService.save(user);
        }

        // 通过验证设置session
        request.getSession().setAttribute("user",existUser.getId());

        return R.success("登录成功!");
    }
}
