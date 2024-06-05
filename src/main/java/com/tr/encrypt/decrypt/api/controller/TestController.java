package com.tr.encrypt.decrypt.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.tr.encrypt.decrypt.api.aspect.annotation.EncryptApi;
import com.tr.encrypt.decrypt.api.aspect.annotation.EncryptResponse;
import com.tr.encrypt.decrypt.api.constant.AESConst;
import com.tr.encrypt.decrypt.api.controller.data.User;
import com.tr.encrypt.decrypt.api.kit.AESKit;
import com.tr.encrypt.decrypt.api.kit.EncryptDataKit;
import com.tr.encrypt.decrypt.api.kit.MD5Kit;
import com.tr.encrypt.decrypt.api.kit.UuidKit;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: TR
 */
@Api(tags = "测试 Api")
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "test success";
    }

    @EncryptResponse
    @GetMapping("/annotation/EncryptResponse")
    public JSONObject annotationEncryptResponse() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("res", "123");
        return jsonObject;
    }

    @EncryptApi
    @GetMapping("/annotation/EncryptApi/{name}")
    public JSONObject annotationEncryptApi(@PathVariable String name) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        return jsonObject;
    }

    @EncryptApi
    @PostMapping("/annotation/EncryptApi/RequestBody")
    public User annotationEncryptApiRequestBody(@RequestBody User user) {
        return user;
    }

    @PostMapping("/annotation/EncryptApi/RequestBody/create/EncryptParam")
    public JSONObject annotationEncryptApiRequestBodyCreateEncryptParam(@RequestBody User user) {
        JSONObject encryptJsonParam = new JSONObject();
        String userJsonString = JSONObject.toJSONString(user);

        // 参数加密
        String encryptParam = AESKit.encrypt(userJsonString, AESConst.KEY);
        // 对 encryptParam 计算 MD5 摘要，服务端对 encryptParam 进行相同 MD5 计算，判断参数传输途中是否被修改
        String md5Digest = MD5Kit.encrypt(encryptParam);
        // 加密签名（格式：时间戳.UUID），服务端用于重放攻击校验（10 分钟内重复的 UUID 视为重放攻击）
        String signature = AESKit.encrypt(System.currentTimeMillis() + "." + UuidKit.getUuid(), AESConst.KEY);

        // 构建 requestParam，格式：encryptParam.md5Digest.signature
        StringBuilder requestParam = new StringBuilder(encryptParam).append(".").append(md5Digest).append(".").append(signature);

        encryptJsonParam.put("requestParam", requestParam.toString());
        return encryptJsonParam;
    }

    @PostMapping("/decrypt/ResponseData")
    public String decryptResponseData(@RequestBody JSONObject jsonObject) {
        return EncryptDataKit.decryptResponseData(jsonObject);
    }

}
