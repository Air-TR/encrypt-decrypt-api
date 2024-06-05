package com.tr.encrypt.decrypt.api.kit;

import com.tr.encrypt.decrypt.api.exception.BusinessException;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: TR
 */
public class RSAKit {

    /** 用于封装随机产生的公钥与私钥 */
    private final static Map<String, String> keyMap = new HashMap<>();
    private static final int KEY_SIZE = 2048; // RSA 算法使用的密钥长度至少应为 2048 位
    private final static String PUBLIC_KEY = "PUBLIC_KEY";
    private final static String PRIVATE_KEY = "PRIVATE_KEY";

    public static void main(String[] args) throws Exception {
        // 生成公钥和私钥
        genKeyPair();
        System.out.println("随机生成的公钥长度为: " + keyMap.get(PUBLIC_KEY).length());
        System.out.println("随机生成的公钥为: " + keyMap.get(PUBLIC_KEY));
        System.out.println("随机生成的私钥长度为: " + keyMap.get(PRIVATE_KEY).length());
        System.out.println("随机生成的私钥为: " + keyMap.get(PRIVATE_KEY));
    }

    /**
     * 随机生成密钥对
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator 类用于生成公钥和私钥对，基于 RSA 算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // RSA 算法使用的密钥长度至少应为 2048 位（随技术的进步，此数值未来还会加大）
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  // 得到私钥
        // 得到公钥字符串
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        keyMap.put(PUBLIC_KEY, publicKeyString);
        keyMap.put(PRIVATE_KEY, privateKeyString);
    }

    /**
     * RSA 加密
     */
    public static String encrypt(String string, String publicKey) {
        try {
            // base64 编码的公钥
            byte[] decoded = Base64.decodeBase64(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            // RSA 加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            String ciphertext = Base64.encodeBase64String(cipher.doFinal(string.getBytes("UTF-8")));
            return ciphertext;
        } catch (Exception e) {
            throw new BusinessException("加密错误");
        }
    }

    /**
     * RSA 解密
     */
    public static String decrypt(String string, String privateKey) {
        try {
            // 64位解码加密后的字符串
            byte[] inputByte = Base64.decodeBase64(string.getBytes("UTF-8"));
            // base64编码的私钥
            byte[] decoded = Base64.decodeBase64(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            // RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            String cleartext = new String(cipher.doFinal(inputByte));
            return cleartext;
        } catch (Exception e) {
            throw new BusinessException("解密错误");
        }
    }

}
