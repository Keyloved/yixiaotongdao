package com.ruider.utils.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailSender {

    private static Logger logger = LoggerFactory.getLogger(EmailSender.class);


    public static boolean sendTextMail(EmailSendInfo mailInfo) {
        boolean sendStatus = false;//发送状态

        // 判断是否需要身份认证
        EmailAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new EmailAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getInstance(pro, authenticator);
        //【调试时使用】开启Session的debug模式
        //sendMailSession.setDebug(true);
        try {
            // 根据session创建一个邮件消息
            MimeMessage  mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            mailMessage.setRecipient(Message.RecipientType.TO,to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject(), "UTF-8");
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // 设置邮件消息的主要内容
            String mailContent = mailInfo.getContent();
            mailMessage.setText(mailContent, "UTF-8");
            mailMessage.saveChanges();

            //生成邮件文件
            //createEmailFile("file/EML_myself-TEXT.eml", mailMessage);

            // 发送邮件
            Transport.send(mailMessage);
            sendStatus = true;
        } catch (MessagingException ex) {
            logger.error("以文本格式发送邮件出现异常", ex);
            return sendStatus;
        }
        return sendStatus;
    }


    /**
     * 生成邮件文件
     */
    public static void createEmailFile(String fileName, Message mailMessage)
            throws MessagingException {

        File f = new File(fileName);
        try {
            mailMessage.writeTo(new FileOutputStream(f));
        } catch (IOException e) {
            logger.error("IOException", e);
        }
    }

    public static void main(String[] args) {
        String fromaddr = "yixiaotongdao@aliyun.com";
        String toaddr = "2771551998@qq.com";
//        String title = "【测试标题】Testing Subject-myself-TEXT";
//        String title = "【测试标题】Testing Subject-myself-HTML";
//        String title = "【测试标题】Testing Subject-myself-eml文件";
        String title = "【测试标题】Testing Subject-myself-eml文件_含多个附件";
        String content = "【测试内容】Hello, this is sample for to check send email using JavaMailAPI ";
        String port = "25";
        String host = "smtp.aliyun.com";
        String userName = "yixiaotongdao@aliyun.com";
        String password = "Mhd080210";

        EmailSendInfo mailInfo = new EmailSendInfo();
        mailInfo.setMailServerHost(host);
        mailInfo.setMailServerPort(port);
        mailInfo.setValidate(true);
        mailInfo.setUserName(userName);
        mailInfo.setPassword(password);
        mailInfo.setFromAddress(fromaddr);
        mailInfo.setToAddress(toaddr);
        mailInfo.setSubject(title);
        mailInfo.setContent(content);
        //mailInfo.setAttachFileNames(new String[]{"file/XXX.jpg","file/XXX.txt"});

        //发送文体格式邮件
        EmailSender.sendTextMail(mailInfo);
        //发送html格式邮件
//         EmailSender.sendHtmlMail(mailInfo);
        //发送含附件的邮件
        //EmailSender.sendTextMail(mailInfo);
        //读取eml文件发送
//         File emailFile = new File("file/EML_myself-eml.eml");
//         File emailFile = new File("file/EML_reademl-eml文件_含文本附件.eml");
//         File emailFile = new File("file/EML_reademl-eml文件_含图片附件.eml");
//         File emailFile = new File("file/EML_reademl-eml文件_含多个附件.eml");
//         EmailSender.sendMail(mailInfo, emailFile);
    }

}
