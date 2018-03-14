package com.hgicreate.rno.ui.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FilterPages implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        String referer = "";

        //获取父url--如果不是直接输入的话就是先前的访问过来的页面，要是用户输入了，这个父url是不存在的
        referer = req.getHeader("REFERER");

        //判断如果上一个目录为空的话，说明是用户直接输入url访问的
        if ("".equals(referer) || null == referer) {
            //当前请求url，去掉几个可以直接访问的页面
            String servletPath = req.getServletPath();

            //跳过index.html和登陆Login.html
            if (servletPath.contains("index.html") || servletPath.contains("login.html")) {
                chain.doFilter(request, response);
            } else {
                resp.reset();
                resp.sendRedirect("/login.html");
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
