package com.tr.encrypt.decrypt.api.config;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author: TR
 */
public class AirHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> parameterMap = new HashMap<>();
    private byte[] body;

    public AirHttpServletRequestWrapper(HttpServletRequest request, JSONObject decryptParams) {
        super(request);
        decryptParams.forEach((k, v) -> {
            parameterMap.put(k, new String[]{String.valueOf(v)});
        });
    }

    public AirHttpServletRequestWrapper(HttpServletRequest request, byte[] body) {
        super(request);
        this.body = body;
    }

    /**
     * 此方法必须（虽然没有自己调，暂不清楚底层哪里调了）
     *  没有这个方法，QueryParam 类型 Api（例如：/encrypt/get/user?name=TR&age=23），进入 Api 后的参数为空，如：User(name=null, age=null)，而实际应该是 User(name=TR, age=23)
     */
    @Override
    public String[] getParameterValues(String name) {
        return parameterMap.getOrDefault(name, super.getParameterValues(name));
    }

    /**
     * 此方法必须
     *  没有这个方法，QueryParam 类型 Api（例如：/encrypt/get/user?name=TR&age=23），解密后的参数无法将密文参数替换
     */
    @Override
    public Enumeration<String> getParameterNames() {
        // 合并原始参数和解密后的参数
        Set<String> paramNames = new HashSet<>(super.getParameterMap().keySet());
        paramNames.addAll(parameterMap.keySet());
        return Collections.enumeration(paramNames);
    }

    /**
     * 此方法必须
     *  没有这个方法，@RequestBody Api 报错：I/O error while reading input message; nested exception is java.io.IOException: Stream closed
     */
    @Override
    public ServletInputStream getInputStream() {
        return new ServletInputStream() {
            private final ByteArrayInputStream bais = new ByteArrayInputStream(body);

            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // 不需要处理，因为不是非阻塞读取
            }
        };
    }

}
