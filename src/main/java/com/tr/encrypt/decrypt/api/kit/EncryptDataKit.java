package com.tr.encrypt.decrypt.api.kit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tr.encrypt.decrypt.api.constant.RSAConst;
import com.tr.encrypt.decrypt.api.exception.BusinessException;

/**
 * @Author: TR
 */
public class EncryptDataKit {

    public static Object encryptResponseData(Object data) {
        try {
            if (data instanceof String) {
                return AESKit.encrypt((String) data);
            }
            JSONObject json = new JSONObject();
            String jsonString = JSON.toJSONString(data);
            String responseData = AESKit.encrypt(jsonString);
            json.put("responseData", responseData);
            json.put("digest", MD5Kit.encrypt(responseData));
            return json;
        } catch (Exception e) {
            throw new BusinessException("Response 加密错误");
        }
    }

    public static String decryptResponseData(JSONObject jsonObject) {
        try {
            String encryptData = jsonObject.getString("responseData");
            String digest = jsonObject.getString("digest");
            if (StringKit.isBlank(digest) || !digest.equals(MD5Kit.encrypt(encryptData))) {
                throw new BusinessException("非法响应数据！");
            }
            return AESKit.decrypt(encryptData);
        } catch (Exception e) {
            throw new BusinessException(e instanceof BusinessException ? e.getMessage() : "Response 解密错误");
        }
    }

    public static JSONObject encryptData(Object data) {
        try {
            JSONObject json = new JSONObject();
            // 1.原始数据
            String jsonString = JSON.toJSONString(data);
            // 2.生成加解密的 key
            String key = UuidKit.getUuid();
            // 3.（AES）用 key 加密原始数据
            String encryptData = AESKit.encrypt(jsonString, key);
            // 4.（RSA）公钥加密 key
            String encryptKey = RSAKit.encrypt(key, RSAConst.PUBLIC_KEY);
            json.put("encryptKey", encryptKey);
            json.put("encryptData", encryptData);
            return json;
        } catch (Exception e) {
            throw new BusinessException("加密数据错误");
        }
    }

    public static String decryptData(JSONObject jsonObject) {
        try {
            String key = RSAKit.decrypt(jsonObject.getString("encryptKey"), RSAConst.PRIVATE_KEY);
            return AESKit.decrypt(jsonObject.getString("encryptData"), key);
        } catch (Exception e) {
            throw new BusinessException("解密数据错误");
        }
    }

}
