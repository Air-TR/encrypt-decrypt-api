package com.tr.encrypt.decrypt.api.controller.filter;

import com.tr.encrypt.decrypt.api.data.User;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: TR
 */
@Api(tags = "User - Filter")
@RestController
public class UserEncryptController {

    /**
     * 调用示例：http://127.0.0.1:8088/encrypt/get/User?name=TR&age=23
     */
    @GetMapping("/encrypt/get/User")
    public User encryptGet(User user) {
        return user;
    }

    /**
     * 调用示例：http://127.0.0.1:8088/encrypt/post/User?name=TR&age=23
     *  结论：
     *  (1) 是否是 queryParam 类型的 Api（形如 ?param= 格式的 Api），与任何请求类型无关
     *  (2) 只要参数没有被 @RequestBody 标注，都会是发送 queryParam 类型的 Api
     */
    @PostMapping("/encrypt/post/User")
    public User encryptPost(User user) {
        return user;
    }

    @PostMapping("/encrypt/User/RequestBody")
    public User encryptPostRequestBody(@RequestBody User user) {
        return user;
    }

}
