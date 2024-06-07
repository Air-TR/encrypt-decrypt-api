package com.tr.encrypt.decrypt.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Author: TR
 */
@Configuration
public class FilterConfig {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    public FilterRegistrationBean<EncryptFilter> encryptFilter() {
        FilterRegistrationBean<EncryptFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new EncryptFilter(stringRedisTemplate));
        registrationBean.addUrlPatterns("/*"); // 设置过滤器应用的 URL 模式
        registrationBean.setOrder(1); // 设置过滤器顺序
        return registrationBean;
    }

//    @Bean
//    public FilterRegistrationBean<DecryptFilter> decryptFilter() {
//        FilterRegistrationBean<DecryptFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new DecryptFilter());
//        registrationBean.addUrlPatterns("/encrypt/*"); // 设置过滤器应用的 URL 模式
//        registrationBean.setOrder(1); // 设置过滤器顺序
//        return registrationBean;
//    }

}