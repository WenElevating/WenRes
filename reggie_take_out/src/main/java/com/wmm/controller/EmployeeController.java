package com.wmm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wmm.common.R;
import com.wmm.entity.Employee;
import com.wmm.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录验证
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        // 密码md5加密
        String password = employee.getPassword();

        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Employee::getUsername,employee.getUsername());

        Employee emp = employeeService.getOne(queryWrapper);

        // 账号是否存在
        if(emp == null){
            return R.error("登录失败");
        }

        // 密码是否正确
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        // 账号是否禁用
        if(emp.getStatus() != 1){
            return R.error("账号已禁用");
        }

        // 查询成功
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){

        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }


    /**
     * 添加员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> addEmployee(HttpServletRequest request,@RequestBody Employee employee){
        log.info("employee = {}"+ employee);
        // 用户是否存在
        LambdaQueryWrapper<Employee>wrapper = new LambdaQueryWrapper();

        wrapper.eq(Employee::getUsername,employee.getUsername());

        Employee queryEmp = employeeService.getOne(wrapper);

        if(queryEmp != null){
            return R.error("该用户已存在");
        }

        // 密码加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 创建时间
        // employee.setCreateTime(LocalDateTime.now());


        // 更新时间
        // employee.setUpdateTime(LocalDateTime.now());


        // 设置创建用户
        // Long empId = (Long) request.getSession().getAttribute("employee");

        // employee.setCreateUser(empId);

        // employee.setUpdateUser(empId);

        boolean isSave = employeeService.save(employee);

        if(isSave){
           return R.success(null);
        }

        return R.error("新增失败");
    }

    /**
     * 分页查询员工信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<IPage<Employee>> QueryEmployeeByPage(Integer page,Integer pageSize,String name){
        log.info("page = {}, pageSize = {}, name = {}",page,pageSize,name);
        // 添加分页条件
        IPage<Employee>currPage = new Page(page,pageSize);

        // 添加过滤条件
        LambdaQueryWrapper<Employee>wrapper = new LambdaQueryWrapper();

        wrapper.like(StringUtils.isNotEmpty(name),Employee::getUsername,name);

        // 添加排序条件
        wrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        currPage = employeeService.page(currPage,wrapper);

        return R.success(currPage);
    }

    /**
     * 根据id修改员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> updateEmployee(HttpServletRequest request,@RequestBody Employee employee){
        log.info("employee = {}",employee.toString());

        long id = Thread.currentThread().getId();
        log.info("线程id = {}",id);

        // 设置修改时间
        // employee.setUpdateTime(LocalDateTime.now());
        // 设置修改用户
        // employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable("id") Long id){
        log.info("id = {}",id);

        Employee employee = employeeService.getById(id);

        if(employee == null){
            return R.error("查询员工信息失败");
        }
        return R.success(employee);
    }
}
