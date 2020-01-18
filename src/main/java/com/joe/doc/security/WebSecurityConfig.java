package com.joe.doc.security;

import cn.hutool.json.JSONUtil;
import com.joe.doc.common.ResponseResult;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.annotation.Resource;
import java.io.PrintWriter;

/**
 * @author Joe BlackZ
 * @description TODO
 * @date 2020/1/18 21:53
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    @Resource
    private UrlAccessDecisionManager urlAccessDecisionManager;

    @Resource
    private AuthenticationAccessDeniedHandler authenticationAccessDeniedHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService);
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers("/v2/api-docs", "/swagger-ui.html");
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .withObjectPostProcessor(this.getObjectPostProcessor())
                .and().formLogin().loginProcessingUrl("/login")
                .usernameParameter("username").passwordParameter("password").permitAll()
                .failureHandler(this.getAuthenticationFailureHandler())
                .successHandler(this.getSuccessHandler())
                .and().logout().permitAll()
                .and().csrf().disable().exceptionHandling().accessDeniedHandler(this.authenticationAccessDeniedHandler);
    }

    private AuthenticationSuccessHandler getSuccessHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> {
            try (PrintWriter printWriter = httpServletResponse.getWriter()) {
                httpServletResponse.setContentType("application/json;charset=utf-8");
                ResponseResult responseResult = ResponseResult.success().msg("Login success").otherData("status", "success");
                String jsonStr = JSONUtil.toJsonStr(responseResult);
                printWriter.write(jsonStr);
                printWriter.flush();
            }
        };
    }

    private AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return (httpServletRequest, httpServletResponse, exception) -> {
            try (PrintWriter printWriter = httpServletResponse.getWriter()) {
                httpServletResponse.setContentType("text/html;charset=UTF-8");
                httpServletResponse.setCharacterEncoding("utf-8");
                ResponseResult responseResult = ResponseResult.fail().otherData("status", "error");
                if (exception instanceof UsernameNotFoundException
                        || exception instanceof BadCredentialsException) {
                    responseResult.msg("Username or password 错误.");
                } else if (exception instanceof DisabledException) {
                    responseResult.msg("账户被禁用，登录失败，请联系管理员!");
                } else {
                    responseResult.msg("登录失败!");
                }
                String jsonStr = JSONUtil.toJsonStr(responseResult);
                printWriter.write(jsonStr);
            }
        };
    }

    private ObjectPostProcessor<FilterSecurityInterceptor> getObjectPostProcessor() {
        return new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                o.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource);
                o.setAccessDecisionManager(urlAccessDecisionManager);
                return o;
            }
        };
    }
}

