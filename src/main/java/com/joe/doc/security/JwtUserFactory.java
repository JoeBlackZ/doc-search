package com.joe.doc.security;

import com.joe.doc.entity.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description jwt user factory
 * @Author JoeBlackZ
 * @Date 2020/1/5 13:48
 */
public class JwtUserFactory {
    private JwtUserFactory() {
    }

    public static JwtUser create(SysUser sysUser) {
        return new JwtUser(
                sysUser.getId(),
                sysUser.getUsername(),
                sysUser.getPassword(),
                mapToGrantedAuthorities(sysUser.getRoles()),
                sysUser.getLastPasswordResetDate()
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<String> authorities) {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
