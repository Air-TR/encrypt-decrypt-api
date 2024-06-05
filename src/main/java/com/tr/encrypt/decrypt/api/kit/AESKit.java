package com.tr.encrypt.decrypt.api.kit;

import com.tr.encrypt.decrypt.api.exception.BusinessException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @Author: TR
 */
public class AESKit {

    private static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";
    public static final String KEY = "AesKey.123456789"; // 必须是 16/24/32 位，分别对应 AES-128、AES-192、AES-256 加密算法

    /**
     * AES 加密，使用默认 Key
     * @param plainText
     */
    public static String encrypt(String plainText) {
        return encrypt(plainText, KEY);
    }

    /**
     * AES 解密，使用默认 Key
     * @param encryptText
     */
    public static String decrypt(String encryptText) {
        return decrypt(encryptText, KEY);
    }

    /**
     * AES 加密，使用自定义 Key
     * @param plainText
     * @param key
     */
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

    /**
     * AES 解密，使用自定义 Key
     * @param encryptText
     * @param key
     */
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
