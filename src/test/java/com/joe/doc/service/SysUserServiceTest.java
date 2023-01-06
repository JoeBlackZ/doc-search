package com.joe.doc.service;

import com.joe.doc.common.ResponseResult;
import com.joe.doc.model.SysUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;


@Slf4j
@SpringBootTest
class SysUserServiceTest {

    @Resource
    private SysUserService sysUserService;

    @Test
    void save() {
        SysUser build = SysUser.builder()
                .username("admin")
                .nickname("Administrator")
                .password("123456")
                .email("zhangqi13c@163.com")
                .phoneNumber("18192149901")
                .build();
        ResponseResult responseResult = this.sysUserService.save(build);
        if (Objects.equals(responseResult.getCode(), 0)) {
            log.info(responseResult.getData().toString());
        } else {
            log.info(responseResult.toString());
        }
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
        ResponseResult responseResult = this.sysUserService.removeByIds(List.of("63b637510fd2d960e02cfb23"));
        log.info(responseResult.toString());
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