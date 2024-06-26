package com.tr.encrypt.decrypt.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.tr.encrypt.decrypt.api.aspect.annotation.NoEncryptResponse;
import com.tr.encrypt.decrypt.api.kit.AESKit;
import com.tr.encrypt.decrypt.api.kit.EncryptDataKit;
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

    @NoEncryptResponse
    @PostMapping("/decrypt/encryptText")
    public String decryptEncryptText(@RequestParam String encryptText) {
        return AESKit.decrypt(encryptText);
    }

    @NoEncryptResponse
    @PostMapping("/decrypt/responseBody")
    public String decryptResponseBody(@RequestBody JSONObject responseBody) {
        return EncryptDataKit.decryptResponseData(responseBody);
    }

}
