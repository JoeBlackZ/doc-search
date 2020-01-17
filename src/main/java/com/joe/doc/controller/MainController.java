package com.joe.doc.controller;

import com.joe.doc.common.ResponseResult;
import com.joe.doc.entity.SysUser;
import com.joe.doc.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @Description  main
 * @author JoeBlackZ
 * @date  2020/1/5 14:39
 */
@Api(tags = "sys")
@RequestMapping("/")
@RestController
public class MainController {

    @Resource
    private SysUserService sysUserService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "username", type = "String", required = true),
            @ApiImplicitParam(name = "password", value = "password", type = "String", required = true)
    })
    @ApiOperation(value = "Log in interface.", notes = "you should provide your username and password")
    @PostMapping("auth/login")
    private ResponseResult login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headName = headerNames.nextElement();
            System.err.println(headName + ": " + request.getHeader(headName));
        }
        return ResponseResult.success().otherData(username, password);
    }


}
