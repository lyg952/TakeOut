package com.sasu.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasu.takeout.common.Result;
import com.sasu.takeout.entity.Employee;
import com.sasu.takeout.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping ("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request , @RequestBody Employee employee){
        //1.取出前端密码  使用md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.更具页面提交的username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee employeeServiceOne = employeeService.getOne(queryWrapper);

        //3.验证是否查到数据
        if(employeeServiceOne == null){
            return Result.error("没有注册账号！登录失败");
        }

        //4.验证密码是否正确
        if(!employeeServiceOne.getPassword().equals(password)){
            return Result.error("密码错误！登录失败");
        }

        //5.验证状态是否可用
        if (employeeServiceOne.getStatus() == 0){
            return Result.error("状态不可用，登录失败");
        }

        //6.登录成功，将员工id存入到session中
        request.getSession().setAttribute("employee",employeeServiceOne.getId());
        return Result.success(employeeServiceOne);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request ){
        //移除Session
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    @PostMapping
    public Result<String> addEmployee(HttpServletRequest request,@RequestBody Employee employee){
        log.info("开始添加员工{}",employee.getUsername());
        //设置密码，采用明文加密的方式
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置创建时间
        //employee.setCreateTime(LocalDateTime.now());
        //设置更改时间
        //employee.setUpdateTime(LocalDateTime.now());

        //设置创建人、修改人
//        Long createUser = (Long)request.getSession().getAttribute("employee");
//        employee.setCreateUser(createUser);
//        employee.setUpdateUser(createUser);

        employeeService.save(employee);
        return Result.success("员工添加成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        //构造分页器
        Page pageInfo = new Page(page,pageSize);

        //构造条件查询构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //姓名条件

        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);



        //执行查询
        employeeService.page(pageInfo,lambdaQueryWrapper);

        return Result.success(pageInfo);
    }

    @PutMapping
    public Result<String> update(HttpServletRequest request,@RequestBody Employee employee){

        Long empId = (Long) request.getSession().getAttribute("employee");

        /*employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());*/

        /*
        * long类型下的id修改有bug，因为employee对象的id是lang类型，有19位，而前端js只能处理精确到16位，这就会导致
        * 服务器发送给浏览器的19位对象id，在经过js在传送给服务器的19位对象id不一致
        * 解决方案：将long类型的数据转化位json String类型的数据给js正常处理，js再将json数据发送给服务器，服务器转化位对象
        * */
        employeeService.updateById(employee);


        return Result.success("修改成功");
    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }
}
