package com.joe.doc.security;

import cn.hutool.json.JSONUtil;
import com.joe.doc.common.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author Joe BlackZ
 * @description AuthenticationAccessDeniedHandler
 * @date 2020/1/18 21:37
 */
@Slf4j
@Component
public class AuthenticationAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
        try (PrintWriter writer = httpServletResponse.getWriter()){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
            ResponseResult responseResult = ResponseResult.fail().msg("Insufficient permissions").otherData("status", "error");
            String jsonStr = JSONUtil.toJsonStr(responseResult);
            writer.write(jsonStr);
            writer.flush();
        }
    }
}
