package com.tr.encrypt.decrypt.api.kit;

import com.alibaba.fastjson.JSONObject;
import com.tr.encrypt.decrypt.api.constant.AESConst;
import com.tr.encrypt.decrypt.api.exception.BusinessException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @Author: TR
 */
public class AESKit {

    private static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";

    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        json.put("account", "gly");
        json.put("password", "cxtd123456");
        String encryptData = encrypt(json.toJSONString(), AESConst.KEY);
        System.out.println(encryptData);
    }

    public static String encrypt(String plainText, String key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    public static String decrypt(String encryptText, String key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptText);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

}
