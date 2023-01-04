package com.joe.doc.controller;

import com.joe.doc.common.ResponseResult;
import com.joe.doc.service.FileInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * @author Joe BlackZ
 */
@Tag(name = "文档管理")
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileInfoService fileInfoService;

    @Operation(summary = "文档列表查询")
    @Parameters({
            @Parameter(name = "page", description = "页码", in = ParameterIn.PATH),
            @Parameter(name = "limit", description = "每页条数", in = ParameterIn.PATH)
    })
    @GetMapping(path = "/fileList")
    public ResponseResult list(@RequestParam(required = false, defaultValue = "1") Integer page,
                               @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return this.fileInfoService.findAllByPage(page, limit);
    }

}
