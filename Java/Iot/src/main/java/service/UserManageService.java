package service;

import com.sun.mail.util.MailSSLSocketFactory;
import dao.UserDao;
import entity.mvc.Result;
import entity.mvc.Token;
import entity.mvc.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;


/**
 * @author aeolus
 * @program SSMDemo
 * @description
 * @date 2021-04-26 11:00:54
 */

@Service
public class UserManageService {
    private final UserDao userDao;

    public UserManageService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    private TokenManager tokenManager;

    public Result login(User loginUser) {
        Result result = new Result();
        List<User> users = userDao.findUser(loginUser);
        User user;
        try {
            user = users.get(0);
        } catch (Exception e) {
            System.out.println("不存在该用户");
            result.setMsg("登录失败，不存在该用户");
            return result;
        }
        if (user.getPassword().equals(loginUser.getPassword())) {
            result.setStatus(0);
            result.setMsg("登录成功");
            Token token = tokenManager.createToken(user);
            result.setData(token.getTokenValue());
        } else {
            result.setMsg("密码错误");
        }
        return result;
    }

    public Result signup(User user) {
        Result result = new Result();
        User temp = new User();
        temp.setName(user.getName());
        if (!userDao.findUser(temp).isEmpty()) {
            result.setMsg("用户已存在");
            return result;
        }
        int affectRowCount = userDao.addUser(user);
        if (affectRowCount == 1) {
            result.setStatus(0);
            result.setMsg("注册成功");
        } else {
            result.setMsg("注册失败");
        }
        return result;
    }

    public Result addUser(User user) {
        Result result = new Result();
        if (!userDao.findUser(user).isEmpty()) {
            result.setMsg("添加用户失败，请检查用户名或邮箱是否重复");
            return result;
        }
        int affectRowCount = userDao.addUser(user);
        if (affectRowCount == 1) {
            result.setStatus(0);
            result.setMsg("添加用户成功");
        } else {
            result.setMsg("添加用户失败");
        }
        return result;
    }

    public Result sendEmail(User user) throws GeneralSecurityException {
        Result result = new Result();
        List<User> users = userDao.findUser(user);
        if (users.isEmpty()){
            result.setMsg("不存在该用户");
            result.setStatus(1);
            return result;
        }
        // 收件人电子邮箱
        String to = user.getEmail();
        // 发件人电子邮箱
        String from = "zsf.d@qq.com";
        String host = "smtp.qq.com";  //QQ 邮件服务器
        // 获取系统属性
        Properties properties = System.getProperties();
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("zsf.d@qq.com", "zjvbfjfddfvwbacj"); //发件人邮件用户名、密码
            }
        });

        try {
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);
            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));
            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            // Set Subject: 头部头字段
            message.setSubject("MyIOT忘记密码找回");
            // 设置消息体
//            message.setText("点击链接重置密码");
            message.setContent("<br/><a href='" + generateResetPwdLink(users.get(0)) +"'>点击重新设置密码</a>","text/html;charset=utf-8");
            // 发送消息
            Transport.send(message);
            System.out.println("Sent email successfully");
            result.setMsg("邮件发送成功，请查收");
            result.setStatus(0);
        } catch (MessagingException mex) {
            mex.printStackTrace();
            result.setMsg("邮件发送失败");
            result.setStatus(1);
        }
        return result;
    }

    private String generateResetPwdLink(User user) {
        Token token = tokenManager.createToken(user);
        return "http://localhost:8080/resetpassword.html?name="
                + user.getName()
                + "&tokenValue=" + token.getTokenValue();
    }

    public Result resetPassword(User user) {
        List<User> users = userDao.findUser(user);
        user.setName(users.get(0).getName());
        user.setAge(users.get(0).getAge());
        user.setEmail(users.get(0).getEmail());
        user.setRole(users.get(0).getRole());
        Result result = updateUserInfo(user);
        if (result.getStatus() == 0){
            result.setMsg("重置密码成功");
        } else {
            result.setMsg("重置密码失败");
        }
        return result;
    }

    public Result findUser(User user) {
        Result result = new Result();
        List<User> users = userDao.findUser(user);
        result.setData(users);
        result.setStatus(0);
        return result;
    }

    public Result updateUserInfo(User user) {
        Result result = new Result();
        User userId = new User();
        userId.setId(user.getId());
        try {
            int affectRowCount = userDao.updateUserInfo(user);
            if (affectRowCount == 1) {
                result.setStatus(0);
                result.setMsg("修改用户信息成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("修改用户信息失败");
        }
        return result;
    }

    public Result deleteUser(User user) {
        Result result = new Result();
        User userBackup = userDao.findUser(user).get(0);
        try {
            userDao.deleteUser(user);
            result.setStatus(0);
            result.setMsg("删除用户完成");
            tokenManager.deleteToken(userBackup);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("删除用户失败");
        }
        return result;
    }


}
