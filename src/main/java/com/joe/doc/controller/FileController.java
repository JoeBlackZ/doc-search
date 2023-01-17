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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    @GetMapping(path = "/list")
    public ResponseResult list(@RequestParam(required = false, defaultValue = "1") Integer page,
                               @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return this.fileInfoService.findAllByPage(page, limit);
    }

    @Operation(summary = "上传文档")
    @Parameter(name = "files", description = "文件", in = ParameterIn.DEFAULT, ref = "file")
    @PostMapping("/upload")
    public ResponseResult upload(@RequestParam("files") List<MultipartFile> files) {
        return this.fileInfoService.upload(files);
    }

    @Operation(summary = "删除文档")
    @DeleteMapping
    public ResponseResult delete(@RequestBody String[] fileInfoIds) {
        return this.fileInfoService.removeByIds(fileInfoIds);
    }

    @Operation(summary = "搜索文档")
    @Parameter(name = "keywords", description = "搜索关键字", in = ParameterIn.QUERY)
    @GetMapping(path = "/search")
    public ResponseResult search(@RequestParam String keywords) {
        return this.fileInfoService.search(keywords);
    }
}
