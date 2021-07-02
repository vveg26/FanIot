package controller;

import controller.interceptor.Admin;
import controller.interceptor.Developer;
import controller.interceptor.Staff;
import entity.mvc.Result;
import entity.mvc.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.UserManageService;

import java.security.GeneralSecurityException;

/**
 * @author aeolus
 * @program SSMDemo
 * @description 测试ssm
 * @date 2021-04-19 11:30:39
 */

@Controller
@RequestMapping("user")
public class UserController {

    private final UserManageService userManageService;

    public UserController(UserManageService userManageService) {
        this.userManageService = userManageService;
    }

    @GetMapping("/forget_password")
    @ResponseBody
    public Result forgetPassword(User user) throws GeneralSecurityException {
        System.out.println("找回密码用户信息：" + user);
        return userManageService.sendEmail(user);
    }

    @PutMapping("/reset_password")
    @ResponseBody
    public Result resetPassword(User user) {
        System.out.println("重置密码用户信息：" + user);
        return userManageService.resetPassword(user);
    }

    @GetMapping("/login")
    @ResponseBody
    public Result login(User user) {
        System.out.println("登录用户信息：" + user);
        return userManageService.login(user);
    }

    @PostMapping("/signup")
    @ResponseBody
    public Result signup(User user) {
        System.out.println("注册用户信息：" + user);
        return userManageService.signup(user);
    }

    @PostMapping("/add")
    @ResponseBody
    @Admin
    public Result addUser(User user) {
        System.out.println("获取待新增用户信息：" + user);
        return userManageService.addUser(user);
    }

    @GetMapping("/find")
    @ResponseBody
    @Admin
    public Result findUser(User user) {
        System.out.println("获取待查询用户信息：" + user);
        return userManageService.findUser(user);
    }

    @GetMapping("/find_self")
    @ResponseBody
    @Staff @Developer
    public Result findSelfUser(@RequestHeader(value = "tokenValue", defaultValue = "0") String tokenValue,
                               User user) {
        System.out.println("获取待查询用户信息：" + user);
        System.out.println("tokenValue = " + tokenValue);
        // 只允许登录用户查询自己的信息
        if (user.getId() == Integer.parseInt(tokenValue.split("_")[0])) {
            return userManageService.findUser(user);
        } else {
            Result result = new Result();
            result.setMsg("无权限");
            return result;
        }
    }


    @PutMapping("/update_self")
    @ResponseBody
    @Staff @Developer
    public Result updateSelfUserInfo(@RequestHeader(value = "tokenValue", defaultValue = "0") String tokenValue,
                                     User user) {
        System.out.println("获取待更新用户信息：" + user);
        System.out.println("tokenValue = " + tokenValue);
        // 只允许登录用户修改自己的信息
        if (user.getId() == Integer.parseInt(tokenValue.split("_")[0])) {
            return userManageService.updateUserInfo(user);
        } else {
            Result result = new Result();
            result.setMsg("无权限");
            return result;
        }
    }

    @PutMapping("/update")
    @ResponseBody
    @Admin
    public Result updateUserInfo(User user) {
        System.out.println("获取待更新用户信息：" + user);
        return userManageService.updateUserInfo(user);
    }

    @DeleteMapping("/delete")
    @ResponseBody
    @Admin
    public Result deleteUser(User user) {
        System.out.println("获取待删除用户信息：" + user);
        return userManageService.deleteUser(user);
    }


}
