package com.joe.doc.controller;

import com.joe.doc.common.ResponseResult;
import com.joe.doc.entity.SysUser;
import com.joe.doc.service.SysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description TODO
 * @Author JoeBlackZ
 * @Date 2020/1/5 14:39
 */
@RequestMapping("/")
@RestController
@PreAuthorize("hasRole('ADMIN')")
public class MainController {

    @Resource
    private SysUserService sysUserService;

    @GetMapping("login")
    private ResponseResult login() {
        return this.sysUserService.findByPage(new SysUser(), 1, 10);
    }


}
