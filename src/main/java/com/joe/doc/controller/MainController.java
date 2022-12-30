package com.joe.doc.controller;

import com.joe.doc.service.SysUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @description  main
 * @author JoeBlackZ
 * @date  2020/1/5 14:39
 */
@RequestMapping("/")
@RestController
public class MainController {

    @Resource
    private SysUserService sysUserService;

}
