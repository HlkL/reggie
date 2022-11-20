package com.hg.reggie.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Random;

/**
 * @author hougen
 * @program Reggie
 * @description 邮箱发送
 * @create 2022-11-16 22:07
 */
@Slf4j
@Component
public class EmailUtil {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")   //从配置文件读取发件人信息
    private String from;

    /**
     * 生产的验证码位数
     */
    private static final int generateVerificationCodeLength=6;

    /**
     * 发送的验证码
     */
    private String code;

    private final String[] metaCode={"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private String getCode() {
        return code;
    }


    private String getContent() {
        return "<h1 style=\"text-align: center\">\n" +
                "  瑞吉外卖\n" +
                "</h1><hr>\n" +
                "<div style=\"text-align: center\">\n" +
                "  <p>\n" +
                "    尊敬的用户：您好！\n" +
                "  </p>\n" +
                "  您正在进行<span style=\"color: red\">注册账号</span>操作，请在验证码有效期输入以下验证码完成操作：\n" +
                "  <h1 style=\"color: red\">"+
                getCode()+"</h1>\n" +
                "  <span style=\"font-size: 1%\">注意：此操作可能会修改您的密码、登录邮箱或绑定手机。如非本人操作，请及时登录并修改密码以保证帐户安全</span>\n" +
                "  <hr>\n" +
                "</div>";
    }

    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder verificationCode = new StringBuilder();
        while (verificationCode.length()<generateVerificationCodeLength){
            int i = random.nextInt(metaCode.length);
            verificationCode.append(metaCode[i]);
        }
        this.code = verificationCode.toString();
        return verificationCode.toString();
    }

    public void sendSimpleMail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        //发件人名称
        message.setFrom(from);
        //收件人邮件地址，可以是多个，使用逗号分割
        message.setTo(to);
        //邮件的主题
        message.setSubject("瑞吉外卖用户登录");
        //邮件的正文内容
        message.setText(this.getContent());

        log.info("开始发送文本邮件...");
        try {
            mailSender.send(message);
            log.info("文本邮件已经发送...");
        } catch (Exception e) {
            log.info("发送文本邮件时发生异常...", e);
        }
    }

    public void sendHtmlMail(String to, File... attachments) {
        MimeMessage message = mailSender.createMimeMessage();

        log.info("开始发送HTML邮件...");
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("瑞吉外卖用户注册");
            helper.setText(this.getContent(), true);

            //添加附件
            if(attachments != null && attachments.length > 0) {
                for(File f : attachments) {
                    helper.addAttachment(f.getName(), f);
                }
            }
            mailSender.send(message);
            log.info("HTML邮件发送成功...");
        } catch (MessagingException e) {
            log.error("发送HTML邮件时发生异常...", e);
        }
    }
}


