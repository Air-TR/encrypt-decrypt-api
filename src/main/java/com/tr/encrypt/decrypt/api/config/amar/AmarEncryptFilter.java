package com.tr.encrypt.decrypt.api.config.amar;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tr.encrypt.decrypt.api.kit.AESKit;
import com.tr.encrypt.decrypt.api.kit.MD5Kit;
import com.tr.encrypt.decrypt.api.kit.StringKit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * @Author: TR
 */
@Slf4j
public class AmarEncryptFilter implements Filter {

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        AmarRequestWrapper request = new AmarRequestWrapper((HttpServletRequest) servletRequest);
        AmarResponseWrapper response = new AmarResponseWrapper((HttpServletResponse) servletResponse);

//        //不需要判断 GET 还是 POST，直接拿出数据解密，然后放回去
//        String encryptSign = request.getHeader("encryptSign");
//        // 解密 destinyKey
//        log.info("获取到签名密文为：" + encryptSign);
//        String signJsonStr = AESKit.decrypt(encryptSign, "AesKey1234567890");
//        log.info("获取到签名明文为：" + signJsonStr);
//        // 请求头 destinyKey 的值判空
//        if (StringKit.isBlank(signJsonStr)) {
//            setResponse(response, "签名不合法");
//            return;
//        }

//        JSONObject signJson = JSON.parseObject(signJsonStr);
        String request_uri = URLDecoder.decode(request.getRequestURI().substring(request.getContextPath().length()),request.getCharacterEncoding());
//        request.setRequestURI("/encrypt/list");

        // get 请求
        String encryptParam = request.getParameter("encryptParam");
        if (StringKit.isNotBlank(encryptParam)) {
            log.info("请求正文密文：" + encryptParam);
            // 这里直接将已经获取到的加密报文移除掉
            request.getParameterMap().remove("encryptParam");

//            if (!checkDigest(signJson, encryptParam, request_uri)) {
//                setResponse(response, "摘要不合法");
//                return;
//            }

            // 解密
//            String param = AESKit.decrypt(encryptParam, "AesKey1234567890");
//            log.info("请求正文明文：" + param);
            JSONObject jsonParam = JSONObject.parseObject(encryptParam);
            // 把解密后的数据重新放入 request
            fillParams(request, jsonParam);
        }

        // post 请求
        String jsonStr = getPostParams(request);
        if (StringKit.isNotBlank(jsonStr)) {
//            log.info("请求正文密文：" + jsonStr);
//            JSONObject json = JSONObject.parseObject(jsonStr);
//
////            if (!checkDigest(signJson, encryptData, request_uri)) {
////                setResponse(response, "摘要不合法");
////                return;
////            }
//
//            String param = AESKit.decrypt(json.getString("json"), AESConst.KEY);
//            log.info("请求正文明文：" + param);
//            request.setRequestBody(param.getBytes(StandardCharsets.UTF_8));
        }

        filterChain.doFilter(request, servletResponse);

        // 需要加密返回响应数据使用 @EncryptResponse

        String responseBody = new String(response.getContent(), StandardCharsets.UTF_8);
        log.info("响应正文明文：" + responseBody);
        // 先给数据加密
        if (StringKit.isNotBlank(responseBody) && HttpServletResponse.SC_OK == response.getStatus()) {
            responseBody = AESKit.encrypt(responseBody);
            log.info("响应正文密文：" + responseBody);
        }
        // 真正传输
        OutputStream out = response.getResponse().getOutputStream();
        out.write(responseBody.getBytes());
        out.close();

    }

    @Override
    public void destroy() {
        // 过滤器销毁
    }

    /**
     * 获取 post/put 请求参数
     */
    public static String getPostParams(AmarRequestWrapper request) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line;
        StringBuffer stringBuffer = new StringBuffer();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        bufferedReader.close();
        return stringBuffer.toString();
    }

    public static void fillParams(AmarRequestWrapper request, JSONObject json) {
        Iterator<String> it = json.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Object paramStr = json.get(key);
            // 匹配到数组
            if(paramStr instanceof JSONArray){
                String[] params = new String[((JSONArray) paramStr).size()];
                // 不会传两种数据类型，因此拿第一个数据判断数据类型
                ((JSONArray) paramStr).stream().map(x -> x + "").collect(Collectors.toList()).toArray(params);
                request.getParameterMap().put(key, params);
            }else {
                request.getParameterMap().put(key, new String[]{json.getString(key)});
            }
        }
    }

    /**
     * 校验摘要 digest
     */
    public static boolean checkDigest(JSONObject json, String encryptData, String requestUri) throws NoSuchAlgorithmException {
        // 计算摘要，连带 uri 一起计算
        String digest = MD5Kit.encrypt( requestUri + encryptData);
        return digest.equals(json.getString("digest"));
    }

    public static void setResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(message);
    }

}