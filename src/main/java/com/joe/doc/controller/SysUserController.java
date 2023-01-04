package com.joe.doc.controller;

import com.joe.doc.common.ResponseResult;
import com.joe.doc.service.SysUserService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * @author Joe BlackZ
 */
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @GetMapping
    public ResponseResult list(@RequestParam(required = false, defaultValue = "1") Integer page,
                               @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return this.sysUserService.findAllByPage(page, limit);
    }

}
