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

/**
 * @author Joe BlackZ
 */
@Api(tags = "sys")
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page", type = "query", dataType = "Integer", defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "limit", type = "query", dataType = "Integer", defaultValue = "20")
    })
    @ApiOperation(value = "Query user list.", notes = "You could provide query parameter of sysUser and page info, page default 1, limit default 20")
    @GetMapping(path = "/")
    public ResponseResult list(@RequestParam(required = false, defaultValue = "1") Integer page,
                               @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return this.sysUserService.findAllByPage(page, limit);
    }

}
