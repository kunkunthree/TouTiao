package com.xiepuxin.toutiao;

import com.xiepuxin.toutiao.util.MailSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = ToutiaoApplication.class)
public class MailTests {
    private static final Logger logger = LoggerFactory.getLogger(MailTests.class);

//    @Autowired
    FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();

    @Test
    public void testMail() throws MessagingException,UnsupportedEncodingException{
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("toutiaoxpx@163.com");
        mailSender.setPassword("toutiao123456");    //授权码，不是密码
        mailSender.setHost("smtp.163.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtp");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
//        javaMailProperties.put("username","xpxmina@163.com");
//        javaMailProperties.put("password","sola123456");
//        javaMailProperties.put("mail.smtp.host","smtp.163.com");
        javaMailProperties.put("mail.smtp.ssl.enable", true);
//        javaMailProperties.put("mail.smtp.auth",true);
//        javaMailProperties.put("mail.transport.protocol","smtp");
        mailSender.setJavaMailProperties(javaMailProperties);

        String to = "522905534@qq.com";
        String subject = "登录异常";
        String template = "mails/loginException.ftl";
        Map<String,Object> model = new HashMap<>();
        model.put("username","sola");
        String nick = MimeUtility.encodeText("xpx");
        InternetAddress from = new InternetAddress(nick + "<toutiaoxpx@163.com>");
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf8");
//            String result = FreeMarkerTemplateUtils.processTemplateIntoString(
//                    freeMarkerConfigurer.getConfiguration().getTemplate(template), model);

//        mimeMessage.addRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText("123",true);
        mailSender.send(mimeMessage);
    }


    @Test
    public void testString(){
        String s = new String("\\u534e\\u4e2d");
        System.out.println(decodeUnicode(s));
    }

    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }
}
