package com.tr.encrypt.decrypt.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: TR
 */
@Configuration
public class FilterConfig {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${ex-path.encrypt-request}")
    private String encryptRequestExPath;

    private static List<String> encryptRequestExPaths = new ArrayList<>();

    @PostConstruct
    public void initSecurityExPath() {
        encryptRequestExPaths = Arrays.stream(encryptRequestExPath.split(",")).collect(Collectors.toList());
    }

    /**
     * 加密请求过滤器
     */
    @Bean
    public FilterRegistrationBean<EncryptRequestFilter> encryptFilter() {
        FilterRegistrationBean<EncryptRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new EncryptRequestFilter(stringRedisTemplate, encryptRequestExPaths));
        registrationBean.addUrlPatterns("/*"); // 设置过滤器应用的 URL 模式
        registrationBean.setOrder(1); // 设置过滤器顺序
        return registrationBean;
    }

}