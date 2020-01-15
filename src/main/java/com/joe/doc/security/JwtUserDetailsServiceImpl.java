package com.joe.doc.security;

import com.joe.doc.entity.SysUser;
import com.joe.doc.repository.SysUserRepository;
import com.joe.doc.security.JwtUserFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description TODO
 * @Author JoezBlackZ
 * @Date 2020/1/5 13:56
 */
@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SysUserRepository sysUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserRepository.selectByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return JwtUserFactory.create(sysUser);
        }
    }

}
