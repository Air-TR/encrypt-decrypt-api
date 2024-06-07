package com.tr.encrypt.decrypt.api.aspect;

import com.tr.encrypt.decrypt.api.aspect.annotation.EncryptApi;
import com.tr.encrypt.decrypt.api.aspect.annotation.EncryptResponse;
import com.tr.encrypt.decrypt.api.aspect.annotation.NoEncryptResponse;
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
        /**
         * 控制方式一：指定需要加密返回的
         *  对加了 @EncryptApi 或 @EncryptResponse 注解的方法加密响应数据
         */
        return methodParameter.hasMethodAnnotation(EncryptApi.class) || methodParameter.hasMethodAnnotation(EncryptResponse.class);

        /**
         * 控制方式二：指定不需要加密返回的（全局加密响应）
         *  对加了 @NoEncryptResponse 注解的方法不加密响应数据
         *
         * 待解决：会将其他响应数据如 Swagger 也加密返回，导致 Swagger 页面打不开
         */
//        return !methodParameter.hasMethodAnnotation(NoEncryptResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> clazz, ServerHttpRequest request, ServerHttpResponse response) {
        return EncryptDataKit.encryptResponseData(data);
    }

}
