package com.joe.doc.security;

import com.joe.doc.entity.SysMenu;
import com.joe.doc.entity.SysRole;
import com.joe.doc.repository.mongo.SysMenuRepository;
import com.joe.doc.repository.mongo.SysRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author JoeBlackZ
 */
@Slf4j
@Component
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Resource
    private SysMenuRepository sysMenuRepository;

    @Resource
    private SysRoleRepository sysRoleRepository;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) {
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        List<SysMenu> sysMenus = this.sysMenuRepository.selectAll();
        for (SysMenu sysMenu : sysMenus) {
            String url = sysMenu.getUrl();
            if (antPathMatcher.match(url, requestUrl)) {
                SysRole querySysRole = SysRole.builder().sysMenus(Collections.singletonList(sysMenu.getId())).build();
                List<SysRole> sysRoleList = this.sysRoleRepository.select(querySysRole);
                List<String> sysRoleNames = new ArrayList<>(sysRoleList.size());
                sysRoleList.forEach(sysRole -> sysRoleNames.add(sysRole.getRoleName()));
                return SecurityConfig.createList(sysRoleNames.toArray(new String[0]));
            }
        }
        //没有匹配上的资源，都是登录访问
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return Collections.emptyList();
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
