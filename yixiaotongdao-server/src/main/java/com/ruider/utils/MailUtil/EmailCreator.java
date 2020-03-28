package com.ruider.utils.MailUtil;

public class EmailCreator {

    public static EmailSendInfo createEmail (String toAddress, String nickName, String content) {
        //固定配置
        String title = "【一校同道】「写给未来的ta」";
        String contents = nickName +"给你写信啦～～" + "\n" + "信件内容：" + content;
        String port = "25";
        String host = "smtp.aliyun.com";
        String userName = "yixiaotongdao@aliyun.com";
        String password = "Mhd080210";
        String fromaddr = "yixiaotongdao@aliyun.com";

        EmailSendInfo mailInfo = new EmailSendInfo();
        mailInfo.setMailServerHost(host);
        mailInfo.setMailServerPort(port);
        mailInfo.setValidate(true);
        mailInfo.setUserName(userName);
        mailInfo.setPassword(password);
        mailInfo.setFromAddress(fromaddr);
        mailInfo.setToAddress(toAddress);
        mailInfo.setSubject(title);
        mailInfo.setContent(contents);

        return mailInfo;
    }
}
