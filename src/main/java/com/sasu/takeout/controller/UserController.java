package com.sasu.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasu.takeout.common.Result;
import com.sasu.takeout.entity.User;
import com.sasu.takeout.service.UserService;
import com.sasu.takeout.utils.BaseContext;
import com.sasu.takeout.utils.SMSUtils;
import com.sasu.takeout.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session){
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("code:{}",code);
            //接入阿里云，发送验证码
            //SMSUtils.sendMessage("短信签名","短信模板编码",phone,code);
            session.setAttribute(phone,code);
            return Result.success("短信验证码发送成功，请注意查收");
        }
        return Result.error("短信验证码发送失败");
    }

    @PostMapping("/login")
    public Result<User> sendMsg(@RequestBody Map map, HttpSession session){
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        Object codeInSession = session.getAttribute(phone);

        if (codeInSession != null && codeInSession.equals(code)){

            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(lambdaQueryWrapper);

            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            BaseContext.setCurrentId(user.getId());
            return Result.success(user);
        }
        return Result.error("登录失败");
    }
}
