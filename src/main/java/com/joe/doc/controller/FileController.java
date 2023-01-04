package com.joe.doc.controller;

import com.joe.doc.service.FileInfoService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * @author Joe BlackZ
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileInfoService fileInfoService;

    @PostMapping(path = "/fileList")
    public Object list(@RequestParam(required = false, defaultValue = "1") Integer page,
                               @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return this.fileInfoService.findAllByPage(page, limit).getData();
    }

}
