package com.joe.doc.service;

import com.joe.doc.entity.SysUser;
import com.joe.doc.repository.BaseRepository;
import com.joe.doc.repository.SysUserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description sys user service
 * @Author JoezBlackZ
 * @Date 2020/1/4 10:08
 */
@Service
public class SysUserService extends BaseService<SysUser> {

    @Resource
    private SysUserRepository sysUserRepository;

    @Override
    public BaseRepository<SysUser> getRepository() {
        return this.sysUserRepository;
    }
}
