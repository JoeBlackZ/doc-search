package com.joe.doc.service;

import cn.hutool.core.util.StrUtil;
import com.joe.doc.common.ResponseResult;
import com.joe.doc.entity.SysUser;
import com.joe.doc.repository.BaseRepository;
import com.joe.doc.repository.SysUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @description sys user service
 * @author JoezBlackZ
 * @date 2020/1/4 10:08
 */
@Service
public class SysUserService extends BaseService<SysUser> {

    @Resource
    private SysUserRepository sysUserRepository;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public BaseRepository<SysUser> getRepository() {
        return this.sysUserRepository;
    }

    public ResponseResult validateUserLogin(String username, String password) {
        if (StrUtil.isEmpty(username)) {
            return ResponseResult.fail().msg("Username is empty.");
        }
        if (StrUtil.isEmpty(password)) {
            return ResponseResult.fail().msg("Password is empty.");
        }
        SysUser sysUser = this.sysUserRepository.selectByUsername(username);
        if (Objects.isNull(sysUser)) {
            return ResponseResult.fail().msg("User does not exist.");
        }
        boolean matches = bCryptPasswordEncoder.matches(password, sysUser.getPassword());
        if (!matches) {
            return ResponseResult.fail().msg("Password is incorrect.");
        }
        return ResponseResult.success().msg("Login in success.").data(sysUser.getId());
    }
}
