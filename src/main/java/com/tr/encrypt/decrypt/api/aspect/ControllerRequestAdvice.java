package com.tr.encrypt.decrypt.api.aspect;

import com.alibaba.fastjson.JSONObject;
import com.tr.encrypt.decrypt.api.aspect.annotation.EncryptApi;
import com.tr.encrypt.decrypt.api.aspect.annotation.EncryptRequest;
import com.tr.encrypt.decrypt.api.constant.RedisKey;
import com.tr.encrypt.decrypt.api.exception.BusinessException;
import com.tr.encrypt.decrypt.api.kit.AESKit;
import com.tr.encrypt.decrypt.api.kit.MD5Kit;
import com.tr.encrypt.decrypt.api.kit.StringKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 待解决：该配置只拦截参数被 @RequestBody 定义的请求，否则不拦截
 *
 * @Author: TR
 */
@RestControllerAdvice
public class ControllerRequestAdvice implements RequestBodyAdvice {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 对加了 @EncryptApi 或 @EncryptRequest 注解的方法执行请求解密
        return methodParameter.hasMethodAnnotation(EncryptApi.class) || methodParameter.hasMethodAnnotation(EncryptRequest.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        // 在读取请求体之前可以进行的操作
        byte[] payload = StreamUtils.copyToByteArray(inputMessage.getBody());

        String jsonStr = new String(payload);
        JSONObject json = JSONObject.parseObject(jsonStr);

        String requestParam = json.getString("requestParam");
        if (StringKit.isBlank(requestParam)) {
            throw new BusinessException("非法参数");
        }

        String[] encrypts = requestParam.split("\\.");

        /**
         * 校验参数是否合法
         *  (1) encrypts 必须是 3 位
         *  (2) MD5 校验参数是否被篡改
         */
        if (encrypts.length != 3 || !encrypts[1].equals(MD5Kit.encrypt(encrypts[0]))) {
            throw new BusinessException("非法参数");
        }

        // 验证签名（用于重放攻击校验）
        String[] signs = AESKit.decrypt(encrypts[2]).split("\\.");
        if (signs.length != 2) {
            throw new BusinessException("非法参数");
        }
        /**
         * 防重放攻击（时间戳）校验
         *  与服务器时间相差超过 5 分钟视为重放攻击（防止攻击者绕过 UUID 重放攻击检验，即等待 Redis 保存的 request_sign 过期后再用发送重复请求）
         */
        if (Math.abs(new Date().getTime() - Long.valueOf(signs[0])) / 1000 > 300) { // 300 秒（5分钟）
            throw new BusinessException("超时重放攻击");
        }
        // 防重放攻击（UUID）校验，10 分钟内相同签名视为重放攻击
        if (stringRedisTemplate.hasKey(RedisKey.REQUEST_SIGN + signs[1])) {
            throw new BusinessException("签名重放攻击");
        }
        // request_sign（请求签名）在 Redis 保存 10 分钟
        stringRedisTemplate.opsForValue().set(RedisKey.REQUEST_SIGN + signs[1], signs[1], 10, TimeUnit.MINUTES);

        // 实际参数（解密后）
        String decryptParam = AESKit.decrypt(encrypts[0]);

        // 返回 HttpInputMessage 匿名对象
        return new HttpInputMessage() {
            @Override
            public HttpHeaders getHeaders() {
                return inputMessage.getHeaders();
            }
            @Override
            public InputStream getBody() {
                // 使用原始数据构建为 ByteArrayInputStream
                return new ByteArrayInputStream(decryptParam.getBytes());
            }
        };
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 读取请求体之后可以进行的操作
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 处理空消息体的逻辑
        return body;
    }

}
