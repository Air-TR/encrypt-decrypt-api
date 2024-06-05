package com.tr.encrypt.decrypt.api.aspect;

import com.alibaba.fastjson.JSONObject;
import com.tr.encrypt.decrypt.api.aspect.annotation.EncryptApi;
import com.tr.encrypt.decrypt.api.aspect.annotation.EncryptResponse;
import com.tr.encrypt.decrypt.api.kit.EncryptDataKit;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @Author: TR
 */
@RestControllerAdvice
public class ControllerResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> clazz) {
        // 对加了 @EncryptApi 或 @EncryptResponse 注解的方法执行响应数据加密
        return methodParameter.hasMethodAnnotation(EncryptApi.class) || methodParameter.hasMethodAnnotation(EncryptResponse.class);
    }

    @Override
    public JSONObject beforeBodyWrite(Object data, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> clazz, ServerHttpRequest request, ServerHttpResponse response) {
        return EncryptDataKit.encryptResponseData(data);
    }

}
