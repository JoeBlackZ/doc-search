package com.joe.doc.controller;

import com.joe.doc.common.ResponseResult;
import com.joe.doc.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @description  main
 * @author JoeBlackZ
 * @date  2020/1/5 14:39
 */
@Slf4j
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
    @ApiOperation(value = "Log in API.", notes = "You should provide your username and password")
    @GetMapping(path = "login", params = {"username", "password"})
    public ResponseResult login(@RequestParam String username, @RequestParam String password) {
        return this.sysUserService.validateUserLogin(username, password);
    }


}
