package cn.cyf.chatclient.modules.system.controller;



import cn.cyf.chatclient.common.pojo.LoginInfo;
import cn.cyf.chatclient.common.pojo.Result;
import cn.cyf.chatclient.modules.system.service.LogService;
import cn.cyf.chatclient.modules.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        log.info("用户登录信息：" + user.getUsername());
        LoginInfo info = logService.login(user);
        if (info != null) {
            log.info("登录成功：{}", info);
            return Result.success(info);

        } else{
            log.info("登录失败：{}", user.getUsername());
            return Result.error("登陆失败，密码或用户名错误");
        }
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        log.info("用户注册信息：{}", user.getUsername());
        boolean result = logService.register(user);
        if (result) {
            log.info("注册成功：{}", user.getUsername());
            return Result.success("注册成功");
        } else {
            log.info("注册失败：{}", user.getUsername());
            return Result.error("注册失败，用户名已存在");
        }
    }
}
