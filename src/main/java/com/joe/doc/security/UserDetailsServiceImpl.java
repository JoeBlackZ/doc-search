package com.joe.doc.security;

import cn.hutool.core.util.StrUtil;
import com.joe.doc.entity.SysUser;
import com.joe.doc.repository.mongo.SysUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author JoeBlackZ
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SysUserRepository sysUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            log.error("Username is illegal: {}", username);
            throw new IllegalArgumentException("Username is " + username);
        }
        SysUser sysUser = this.sysUserRepository.selectByUsername(username);
        if (sysUser == null) {
            log.error("Could not find sysUser by this username: {}", username);
            throw new UsernameNotFoundException("Username is " + username);
        }
        return new UserDetailsImpl(sysUser);
    }
}
