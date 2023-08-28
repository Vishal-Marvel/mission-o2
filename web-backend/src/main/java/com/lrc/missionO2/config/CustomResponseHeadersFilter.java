package com.lrc.missionO2.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

//@Component
//@WebFilter("/*")  // Apply the filter to all requests
@Order(Ordered.HIGHEST_PRECEDENCE)  // Set the filter order
public class CustomResponseHeadersFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code (if needed)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        // Set the desired response headers
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*"); // Allow requests from all origins
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");

        // Continue with the filter chain
        chain.doFilter(request, (ServletResponse) httpServletResponse);
    }

    @Override
    public void destroy() {
        // Cleanup code (if needed)
    }
}
