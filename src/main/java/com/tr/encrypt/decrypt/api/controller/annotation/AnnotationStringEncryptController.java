package com.tr.encrypt.decrypt.api.controller.annotation;

import com.tr.encrypt.decrypt.api.aspect.annotation.EncryptApi;
import com.tr.encrypt.decrypt.api.aspect.annotation.EncryptRequest;
import com.tr.encrypt.decrypt.api.aspect.annotation.EncryptResponse;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 使用这个需要保证请求不会进入 EncryptFilter
 *  (1) 关闭 EncryptFilter
 *  (2) 限制 EncryptFilter 过滤 URL
 *
 * @Author: TR
 */
@Api(tags = "String - Annotation")
@RestController
public class AnnotationStringEncryptController {

    @EncryptResponse
    @GetMapping("/String/EncryptResponse/PathVariable/{name}")
    public String encryptResponsePathVariable(@PathVariable String name) {
        return "Hi, " + name;
    }

    /**
     * 参数未被 @RequestBody 定义，不会走请求解密，直接进入 Controller
     */
    @EncryptRequest
    @GetMapping("/String/EncryptRequest/PathVariable/{name}")
    public String encryptRequestPathVariable(@PathVariable String name) {
        return "Hi, " + name;
    }

    /**
     * 参数未被 @RequestBody 定义，不会走请求解密，直接进入 Controller
     */
    @EncryptApi
    @GetMapping("/String/EncryptApi/PathVariable/{name}")
    public String encryptApiPathVariable(@PathVariable String name) {
        return "Hi, " + name;
    }

    /**
     * 参数未被 @RequestBody 定义，不会走请求解密，直接进入 Controller
     */
    @EncryptApi
    @GetMapping("/String/EncryptApi/RequestParam")
    public String encryptApiRequestParam(@RequestParam String name) {
        return "Hi, " + name;
    }

}
