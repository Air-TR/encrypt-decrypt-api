package com.tr.encrypt.decrypt.api.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注不需要加密响应数据的接口（返回原始数据，不被加密）
 *
 * @Author: TR
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NoEncryptResponse {
}
