package com.tr.encrypt.decrypt.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.tr.encrypt.decrypt.api.kit.AESKit;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: TR
 */
@Api(tags = "解密数据")
@RestController
public class DecryptController {

    @PostMapping("/decrypt/string")
    public String decryptString(@RequestParam String encryptText) {
        return AESKit.decrypt(encryptText);
    }

    @PostMapping("/decrypt/responseBody")
    public String decryptJson(@RequestBody JSONObject responseBody) {
        return AESKit.decrypt(responseBody.toJSONString());
    }

}