package com.xiepuxin.toutiao.util;

import com.xiepuxin.toutiao.controller.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

@Service
public class MailSender implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    private JavaMailSenderImpl mailSender;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("toutiaoxpx@163.com");
        mailSender.setPassword("toutiao123456");    //授权码，不是密码
        mailSender.setHost("smtp.163.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtp");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }

    public boolean sendWithHTMLTemplate(String to,
                                        String subject,
                                        String template,
                                        Map<String,Object> model){
        try{
            String nick = MimeUtility.encodeText("xpx");
            InternetAddress from = new InternetAddress(nick+"<toutiaoxpx@163.com>");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,"utf8");
            String result = FreeMarkerTemplateUtils.processTemplateIntoString(
                    freeMarkerConfigurer.getConfiguration().getTemplate(template),model);

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(result,true);
            mailSender.send(mimeMessage);
            logger.info("发送成功");
            return true;
        }catch(Exception e){
            logger.error("发送邮件失败："+e.getMessage());
            return false;
        }
    }
}
