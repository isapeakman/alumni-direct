package com.lightcs.service.impl;

import com.lightcs.service.VerifyService;
import com.lightcs.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author: peak-like
 * @CreateTime: 2025-04-23
 * @Description:
 * @Version: 1.0
 */

@Service
public class VerifyServiceImpl implements VerifyService {
    @Resource
    JavaMailSender sender;  //一个用于发送邮箱的类
    @Autowired
    RedisUtil redisUtil;   //用于Redis数据库操作
    @Value("${spring.mail.username}")
    String email;

    @Override
    public void sendVerifyCode(String mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件标题
        message.setSubject("【校友直聘网站】您的注册码");
        //设置随机数作为验证码
        Random random = new Random();
        int code = random.nextInt(89999) + 10000;//小心机
        //用redis保存验证码
        //提前验证一下，如果发送了多次则删除上一次的验证码，保留最新的验证码
        if (redisUtil.get("verify:code:" + mail) != null) {
            redisUtil.del("verify:code:" + mail);
        }
        redisUtil.set("verify:code:" + mail, code + "", 5, TimeUnit.MINUTES);//设置五分钟过期时间
        //邮件内容
        message.setText("您的验证码是：" + code + "，五分钟有效，请及时完成注册。若不是本人操作请忽略");
        message.setFrom(email); //谁发送，必须和yaml文件中的账号一致
        message.setTo(mail);   //谁接收
        sender.send(message);
    }

    @Override
    public boolean doVerify(String mail, String code) {
        String string = (String) redisUtil.get("verify:code:" + mail);
        // 校验成功后删除验证码
        if (string.equals(code)) {
            redisUtil.del("verify:code:" + mail);
            return true;
        }
        return false;
    }
}


