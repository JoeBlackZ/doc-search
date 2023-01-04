package com.joe.doc.repository;

import com.joe.doc.model.SysUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
class SysUserRepositoryTest {

    @Resource
    private SysUserRepository sysUserRepository;

    @Test
    void insert() {
        SysUser sysUser = SysUser.builder()
                .username("admin")
                .password("123456")
                .nickname("Administrator")
                .email("zhangqi13c@163.com")
                .build();
        SysUser insert = this.sysUserRepository.insert(sysUser);
        log.info(insert.getId());
    }

    @Test
    void insertAll() {
        SysUser sysUser1 = SysUser.builder().username("Tom Hank").phoneNumber("12345678941").build();
        SysUser sysUser2 = SysUser.builder().username("Alon Musk").phoneNumber("12345678941").build();
        List<SysUser> sysUsers = this.sysUserRepository.insertAll(Arrays.asList(sysUser1, sysUser2));
        sysUsers.forEach(sysUser -> log.info(sysUser.getId()));
    }

    @Test
    void updateById() {
        SysUser sysUser = SysUser.builder().username("aaaa").email("zhangqi13c@gmain.com").build();
        sysUser.setId("5e0f57bf17cb034f98386a0e");
        long updateCount = this.sysUserRepository.updateById(sysUser);
        log.info(String.valueOf(updateCount));
    }

    @Test
    void deleteByIds() {
        long l = this.sysUserRepository.deleteById("5e0f57bf17cb034f98386a0f");
        log.info("delete count: {}", l);
    }

    @Test
    void testDeleteByIds() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void findAll() {

    }

    @Test
    void findAllByPage() {
    }

    @Test
    void find() {
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
    void count() {
    }

    @Test
    void testCount() {
    }
}