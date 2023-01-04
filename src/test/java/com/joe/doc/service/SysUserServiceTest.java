package com.joe.doc.service;

import cn.hutool.core.collection.CollUtil;
import com.joe.doc.common.ResponseResult;
import com.joe.doc.model.SysUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.ArrayList;

@Slf4j
@SpringBootTest
class SysUserServiceTest {

    @Resource
    private SysUserService sysUserService;

    @Test
    void save() {
        ArrayList<String> roleList = CollUtil.newArrayList("ADMIN");
        SysUser build = SysUser.builder()
                .username("admin")
                .nickname("Administrator")
                .password("123456")
                .roles(roleList)
                .build();
        ResponseResult save = this.sysUserService.save(build);
        log.info(save.getData().toString());
    }

    @Test
    void saveAll() {
    }

    @Test
    void modifyById() {
    }

    @Test
    void removeByIds() {
    }

    @Test
    void testRemoveByIds() {
    }

    @Test
    void removeById() {
    }

    @Test
    void findAllByPage() {
        log.info(new ObjectId().toString());
        ResponseResult allByPage = this.sysUserService.findAllByPage(1, 10);
        log.info(String.valueOf(allByPage));
    }

    @Test
    void find() {
        SysUser sysUser = SysUser.builder().username("Joe BlackZ").build();
        ResponseResult responseResult = this.sysUserService.find(sysUser);
        log.info(String.valueOf(responseResult));
    }

    @Test
    void findByPage() {
    }

    @Test
    void findOne() {
    }

    @Test
    void findById() {
    }

    @Test
    void getCount() {
    }

    @Test
    void testGetCount() {
    }
}