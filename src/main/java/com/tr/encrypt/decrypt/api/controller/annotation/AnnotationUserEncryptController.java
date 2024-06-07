package com.tr.encrypt.decrypt.api.controller.annotation;

import com.tr.encrypt.decrypt.api.aspect.annotation.EncryptApi;
import com.tr.encrypt.decrypt.api.data.User;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 使用这个需要保证请求不会进入 EncryptFilter
 *  (1) 关闭 EncryptFilter
 *  (2) 限制 EncryptFilter 过滤 URL
 *
 * @Author: TR
 */
@Api(tags = "User - Annotation")
@RestController
public class AnnotationUserEncryptController {

    /**
     * 参数未被 @RequestBody 定义，不会走请求解密，直接进入 Controller
     */
    @EncryptApi
    @GetMapping("/get/User/EncryptApi")
    public User getEncryptApi(User user) {
        return user;
    }

    /**
     * 参数未被 @RequestBody 定义，不会走请求解密，直接进入 Controller
     */
    @EncryptApi
    @PostMapping("/post/User/EncryptApi")
    public User postEncryptApi(User user) {
        return user;
    }

    /**
     * 参数被 @RequestBody 定义，会先走请求解密，再进入 Controller
     */
    @EncryptApi
    @PostMapping("/post/User/EncryptApi/RequestBody")
    public User postEncryptApiRequestBody(@RequestBody User user) {
        return user;
    }

}
