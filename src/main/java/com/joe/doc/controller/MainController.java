package com.joe.doc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @description  main
 * @author JoeBlackZ
 * @date  2020/1/5 14:39
 */
@RequestMapping("/")
@Controller
public class MainController {

    @GetMapping
    public String index() {
        return "index";
    }

}
