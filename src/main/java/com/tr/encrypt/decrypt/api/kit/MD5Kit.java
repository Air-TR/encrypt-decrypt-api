package com.tr.encrypt.decrypt.api.kit;

import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * @Author: TR
 */
public class MD5Kit {

    private static final String SALT = "Salt.123789";

    /**
     * Md5 加密，默认盐值
     */
    public static String encrypt(String string) {
        return encryptWithSalt(string, SALT);
    }

    /**
     * Md5 加密，自定义盐值
     */
    public static String encryptWithSalt(String string, String salt) {
        return new Md5Hash(string, salt).toString();
    }

}
