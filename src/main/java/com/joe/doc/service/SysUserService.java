package com.joe.doc.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.joe.doc.common.ResponseResult;
import com.joe.doc.model.SysUser;
import com.joe.doc.repository.BaseRepository;
import com.joe.doc.repository.SysUserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * @author JoezBlackZ
 * @description sys user service
 * @date 2020/1/4 10:08
 */
@Service
public class SysUserService extends BaseService<SysUser> {

    @Resource
    private SysUserRepository sysUserRepository;

    @Override
    public BaseRepository<SysUser> getRepository() {
        return this.sysUserRepository;
    }

    @Override
    public ResponseResult save(SysUser sysUser) {
        String username = sysUser.getUsername();
        SysUser sysUserData = this.sysUserRepository.selectByUsername(username);
        if (Objects.nonNull(sysUserData)) {
            return ResponseResult.fail().msg("用户名[" + username + "]已存在");
        }
        String password = sysUser.getPassword();
        if (Objects.nonNull(password)) {
            String md5HexPassword = DigestUtil.md5Hex(password);
            sysUser.setPassword(md5HexPassword);
        }
        return super.save(sysUser);
    }
}
