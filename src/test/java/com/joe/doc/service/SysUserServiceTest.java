package com.joe.doc.service;

import cn.hutool.core.collection.CollUtil;
import com.joe.doc.common.ResponseResult;
import com.joe.doc.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
class SysUserServiceTest {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void save() {
        String encode = bCryptPasswordEncoder.encode("123456");
        ArrayList<String> roleList = CollUtil.newArrayList("ADMIN");
        SysUser build = SysUser.builder()
                .username("admin")
                .nickname("Administrator")
                .password(encode)
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
        ResponseResult allByPage = this.sysUserService.findAllByPage(1, 10);
        log.info(String.valueOf(allByPage));
    }

    @Test
    void find() {
        ResponseResult joe_blackZ = this.sysUserService.find(SysUser.builder().username("Joe BlackZ").build());
        log.info(String.valueOf(joe_blackZ));
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