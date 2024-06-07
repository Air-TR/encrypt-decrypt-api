package com.tr.encrypt.decrypt.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.tr.encrypt.decrypt.api.aspect.annotation.NoEncryptResponse;
import com.tr.encrypt.decrypt.api.kit.AESKit;
import com.tr.encrypt.decrypt.api.kit.MD5Kit;
import com.tr.encrypt.decrypt.api.kit.UuidKit;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: TR
 */
@Api(tags = "加密请求参数")
@RestController
public class EncryptParamController {

    @NoEncryptResponse
    @PostMapping("/create/encryptRequestParam")
    public String createEncryptQueryParam(@RequestParam String param) {
        // 原始参数加密
        String encryptParam = AESKit.encrypt(param);
        // 对 encryptParam 计算 MD5 摘要，服务端对 encryptParam 进行相同 MD5 计算，判断参数传输途中是否被修改
        String md5Digest = MD5Kit.encrypt(encryptParam);
        // 加密签名（格式：时间戳.UUID），服务端用于重放攻击校验（10 分钟内重复的 UUID 视为重放攻击）
        String signature = AESKit.encrypt(System.currentTimeMillis() + "." + UuidKit.getUuid());

        // 构建 requestParam，格式：encryptParam.md5Digest.signature
        StringBuilder requestParam = new StringBuilder(encryptParam).append(".").append(md5Digest).append(".").append(signature);

        return requestParam.toString();
    }

    @NoEncryptResponse
    @PostMapping("/create/encryptRequestBody")
    public JSONObject createEncryptRequestBody(@RequestBody JSONObject requestBody) {
        // 原始参数加密
        String encryptParam = AESKit.encrypt(requestBody.toJSONString());
        // 对 encryptParam 计算 MD5 摘要，服务端对 encryptParam 进行相同 MD5 计算，判断参数传输途中是否被修改
        String md5Digest = MD5Kit.encrypt(encryptParam);
        // 加密签名（格式：时间戳.UUID），服务端用于重放攻击校验（10 分钟内重复的 UUID 视为重放攻击）
        String signature = AESKit.encrypt(System.currentTimeMillis() + "." + UuidKit.getUuid());

        // 构建 requestParam，格式：encryptParam.md5Digest.signature
        StringBuilder requestParam = new StringBuilder(encryptParam).append(".").append(md5Digest).append(".").append(signature);

        JSONObject encryptJsonParam = new JSONObject();
        encryptJsonParam.put("requestParam", requestParam.toString());
        return encryptJsonParam;
    }

}
