package com.itfenbao.plugin1.interceptors;

import com.github.snice.spring.pf4j.annotation.Path;
import org.springframework.web.servlet.HandlerInterceptor;
import xyz.erupt.core.constant.EruptRestPath;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Path(value = EruptRestPath.ERUPT_API + "/**")
public class TestInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("Plugin Interceptor preHandle");
        return true;
    }
}
