package com.tr.encrypt.decrypt.api.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 此控制器加密请求解密，走 FilterConfig 方式
 *
 * @Author: TR
 */
@Api(tags = "加密 Api（FilterConfig）")
@RestController
public class EncryptControllerForFilterConfig {

    @GetMapping("/encrypt/test")
    public String test() {
        return "encrypt success";
    }

    @GetMapping("/encrypt/hi/{name}")
    public String hi(@PathVariable String name) {
        return "Hi, " + name;
    }

}
