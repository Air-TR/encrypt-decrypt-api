package com.tr.encrypt.decrypt.api.config;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Map;

/**
 * @Author: TR
 */
public class DecryptFilter implements Filter {

//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // 过滤器初始化
//    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            chain.doFilter(new DecryptRequestWrapper(httpServletRequest), response);
        }
    }

    @Override
    public void destroy() {
        // 过滤器销毁
    }

    private static class DecryptRequestWrapper extends HttpServletRequestWrapper {

        private final HttpServletRequest request;

        public DecryptRequestWrapper(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        @Override
        public String getParameter(String name) {
            String value = request.getParameter(name);
            if (value != null) {
                // 这里调用解密方法
                value = decrypt(value);
            }
            return value;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> paramMap = request.getParameterMap();
            for (String key : paramMap.keySet()) {
                String[] values = paramMap.get(key);
                for (int i = 0; i < values.length; i++) {
                    values[i] = decrypt(values[i]);
                }
            }
            return paramMap;
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            for (int i = 0; i < values.length; i++) {
                values[i] = decrypt(values[i]);
            }
            return values;
        }

        private String decrypt(String value) {
            // 实现解密逻辑
            // 返回解密后的字符串
            return value; // 示例中直接返回原始值，实际应用中需要替换为解密后的值
        }
    }

}