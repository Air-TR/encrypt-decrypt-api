package com.tr.encrypt.decrypt.api.kit;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: TR
 */
public class HttpKit {

    public static HashMap getRequest(String apiUrl) {
        HashMap hashMap = new HashMap();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            // 创建 URL 对象
            URL url = new URL(apiUrl.trim());
            // 打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法类型
            connection.setRequestMethod("GET");
            // 发送请求
            int responseCode = connection.getResponseCode();
            // 检查响应码
            StringBuilder response = null;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            hashMap.put("responseCode", responseCode);
            hashMap.put("responseBody", response);
        } catch (Exception e) {
            e.printStackTrace();
            hashMap.put("responseCode", -1); // 未知异常
        } finally {
            if (Objects.nonNull(reader)) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Objects.nonNull(connection)) {
                connection.disconnect();
            }
        }
        return hashMap;
    }

    public static JSONObject postRequest(String apiUrl, String token, JSONObject paramBody) {
        JSONObject resultJson = new JSONObject();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        OutputStream outputStream = null;
        try {
            // 创建 URL 对象
            URL url = new URL(apiUrl.trim());
            // 创建 HttpURLConnection 对象
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法类型
            connection.setRequestMethod("POST");
            // 设置请求头部信息
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            if (StringKit.isNotBlank(token)) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }
            // 启用输出流
            connection.setDoOutput(true);
            // 创建请求体
            String requestBody = paramBody.toString();
            // 将请求体写入输出流
            outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
            // 获取响应码
            int responseCode = connection.getResponseCode();
            // 读取响应内容
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }
            // 将响应内容输出到控制台
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            resultJson.put("responseCode", responseCode);
            resultJson.put("responseBody", response);
        } catch (Exception e) {
            e.printStackTrace();
            resultJson.put("responseCode", -1); // 未知异常
        } finally {
            if (Objects.nonNull(outputStream)) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Objects.nonNull(reader)) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Objects.nonNull(connection)) {
                connection.disconnect();
            }
        }
        return resultJson;
    }

    public static int uploadFile(String apiUrl, String token, MultipartFile file, Map<String, String> paramMap) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        if (!CollectionUtils.isEmpty(paramMap)) paramMap.forEach(body::add);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl.trim(), requestEntity, String.class);
        return response.getStatusCodeValue();
    }

    public static int upload(String apiUrl, String token, Map<String, String> paramMap) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(token);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        if (!CollectionUtils.isEmpty(paramMap)) paramMap.forEach(body::add);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl.trim(), requestEntity, String.class);
        return response.getStatusCodeValue();
    }

    public static void setResponse(HttpServletResponse response, Integer status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/text");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(message);
    }

}
