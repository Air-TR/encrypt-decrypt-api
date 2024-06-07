package com.tr.encrypt.decrypt.api.config;

import com.alibaba.fastjson.JSONObject;
import com.tr.encrypt.decrypt.api.constant.RedisKey;
import com.tr.encrypt.decrypt.api.exception.BusinessException;
import com.tr.encrypt.decrypt.api.kit.AESKit;
import com.tr.encrypt.decrypt.api.kit.HttpKit;
import com.tr.encrypt.decrypt.api.kit.MD5Kit;
import com.tr.encrypt.decrypt.api.kit.StringKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 加密请求过滤器（将请求解密后再进入 controller）
 *
 * @Author: TR
 */
@Slf4j
public class EncryptRequestFilter implements Filter {

    private List<String> exPaths;

    private StringRedisTemplate stringRedisTemplate;

    public EncryptRequestFilter(StringRedisTemplate stringRedisTemplate, List<String> exPaths) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.exPaths = exPaths;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        // 只对 HttpServletRequest 进行过滤
        if (!(servletRequest instanceof HttpServletRequest) || exPaths.contains("/**")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // 放行 exPath 匹配的 uri
        for (String exPath : exPaths) {
            if (request.getRequestURI().equals(exPath) || (exPath.endsWith("/**") && (request.getRequestURI().startsWith(exPath.replace("/**", "/")) || request.getRequestURI().equals(exPath.replace("/**", ""))))) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }

        try {
            // 处理 QueryString
            String queryString = request.getQueryString(); // 获取查询字符串（?后的内容）
            if (StringKit.isNotBlank(queryString)) {

                // 验证加密请求合法性，返回解密后的参数
                String decryptParam = validateAndDecrypt(queryString);

                // 原始参数转为 Json
                JSONObject decryptParams = JSONObject.parseObject(decryptParam);

                // 创建一个新的 HttpServletRequestWrapper，其中请求体是解密后的内容
                AirHttpServletRequestWrapper airRequest = new AirHttpServletRequestWrapper(request, decryptParams);

                // 继续处理请求链
                filterChain.doFilter(airRequest, servletResponse);
                return;
            }

            // 处理 RequestBody
            String requestBody = readRequestBody(request);
            if (StringKit.isNotBlank(requestBody)) {

                // 解密请求体（这里假设请求体是加密的）
                JSONObject requestBodyJson = JSONObject.parseObject(requestBody);

                String requestParam = requestBodyJson.getString("requestParam");
                if (StringKit.isBlank(requestParam)) {
                    throw new BusinessException("非法参数");
                }

                // 验证加密请求合法性，返回解密后的参数
                String decryptParam = validateAndDecrypt(requestParam);

                // 创建一个新的 HttpServletRequestWrapper，其中请求体是解密后的内容
                AirHttpServletRequestWrapper airRequest = new AirHttpServletRequestWrapper(request, decryptParam.getBytes());

                // 继续过滤器链
                filterChain.doFilter(airRequest, servletResponse);
                return;
            }
        } catch (Exception e) {
            HttpKit.setResponse((HttpServletResponse) servletResponse, 8088, e.getMessage()); // 8088 —— 请求解密异常返回码（自定义，非官方）
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String validateAndDecrypt(String encryptParam) {
        String[] encrypts = encryptParam.split("\\.");

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

        // 返回解密后的原始参数
        return AESKit.decrypt(encrypts[0]);
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return stringBuilder.toString();
    }

}