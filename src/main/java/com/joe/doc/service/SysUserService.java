package com.joe.doc.service;

import com.joe.doc.model.SysUser;
import com.joe.doc.repository.BaseRepository;
import com.joe.doc.repository.SysUserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description sys user service
 * @author JoezBlackZ
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

}
