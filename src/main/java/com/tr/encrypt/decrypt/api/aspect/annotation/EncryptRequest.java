package com.tr.encrypt.decrypt.api.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注加密请求接口（请求参数自动解密）
 *  注：被标注的 Api 必须使用加密参数调用
 *
 * @Author: TR
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface EncryptRequest {
}
