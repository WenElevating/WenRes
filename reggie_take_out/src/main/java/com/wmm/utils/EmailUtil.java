package com.wmm.utils;

import com.wmm.common.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 发送邮件工具类
 */
@Slf4j
@Component //将当前类放入组件中，从而能够实现自动注入
public class EmailUtil {

    //引入邮件接口
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送邮件接口
     * @param toEmail
     */
    public boolean sendEmail(String toEmail){
        String from = "wmmayy@qq.com";
        //创建邮件
        SimpleMailMessage message = new SimpleMailMessage();
        //设置发件人信息
        message.setFrom(from);
        //发给谁
        message.setTo(toEmail);
        message.setSubject("您本次的验证码是");
        //生成六位随机验证码
        String verCode = VerCodeGenerateUtil.generateVerCode();
        TimeAndVerCode.verCodeMap.put(toEmail,verCode);
        //获得当前时间
        //TimeAndVerCode.currentTime = new Date();
        TimeAndVerCode.currentTimeMap.put(toEmail,new Date());

        message.setText("尊敬的用户,您好:\n"
                + "\n本次请求的邮件验证码为:" + verCode + ",本验证码 1 分钟内效，请及时输入。（请勿泄露此验证码）\n"
                + "\n如非本人操作，请忽略该邮件。\n(这是一封通过自动发送的邮件，请不要直接回复）");

        mailSender.send(message);

        return true;
    }


    public boolean emailVerify(String email,String verifyCode){
        // 判断验证码是否过期

        // 日期格式化
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date verifyDate = TimeAndVerCode.currentTimeMap.get(email);

        Date currentDate = new Date();

        if(verifyDate == null || currentDate == null){
            throw new CustomException("验证码未发送!");
        }

        df.format(verifyDate);

        df.format(currentDate);

        log.info("currentDate = {}",currentDate.getTime());

        log.info("verifyDate = {}",verifyDate.getTime());

        log.info("res = {}",currentDate.getTime() - verifyDate.getTime());

        if(currentDate.getTime() - verifyDate.getTime() > 60000){
            TimeAndVerCode.removeCodeAndTime(email);
            return false;
        }

        // 比对验证码
        String mapCode = TimeAndVerCode.verCodeMap.get(email);

        if(!mapCode.equals(verifyCode)){
            TimeAndVerCode.removeCodeAndTime(email);
            return false;
        }


        TimeAndVerCode.removeCodeAndTime(email);
        return true;
    }
}
