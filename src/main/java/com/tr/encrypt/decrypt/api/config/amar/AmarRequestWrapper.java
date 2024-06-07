package com.tr.encrypt.decrypt.api.config.amar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AmarRequestWrapper extends HttpServletRequestWrapper {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, String[]> parameterMap;
    private byte[] requestBody;
    private String servletPath;
    private String requestURI;

    public AmarRequestWrapper(HttpServletRequest request) {
        super(request);
        try {
            requestBody = StreamUtils.copyToByteArray(request.getInputStream());
        } catch (IOException e) {
            logger.error("请求包装类构造报错！",e);
            e.printStackTrace();
        }
        parameterMap = initMap(requestBody,request);
    }

    /**
     * 解决 application/x-www-form-urlencoded 问题
     * @return
     */
    private Map<String,String[]> initMap(byte[] requestBody,HttpServletRequest request) {
        Map<String,String[]> map = new HashMap<>();
        String contentType = (request.getContentType()==null?"":request.getContentType());
        if(requestBody!=null&&contentType.indexOf("x-www-form-urlencoded")>0 && "POST".equals(request.getMethod().toUpperCase())){
            try {
                String bodyStr = new String(requestBody,request.getCharacterEncoding());
                String[] keyValues = bodyStr.split("&");
                for(int i=0;i<keyValues.length;i++){
                    String[] keyAndValue = keyValues[i].split("=");
                    if(keyAndValue.length>1) map.put(keyAndValue[0],new String[]{URLDecoder.decode(keyAndValue[1],"UTF-8")});
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else if(request.getParameterMap().size()>0){
            Map<String,String[]> requestMap = request.getParameterMap();
            map.putAll(requestMap);
        }
        return map;
    }

    @Override
    public String getParameter(String name) {
        return parameterMap.get(name) == null ? null : parameterMap.get(name)[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Set<String> keys = parameterMap.keySet();
        Iterator<String> it = keys.iterator();
        return new AmarsoftEnumeration(it);
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameterMap.get(name);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public ServletInputStream getInputStream(){
        if (requestBody == null) {
            requestBody = new byte[0];
        }
        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    @Override
    public String getServletPath() {
        if(this.servletPath==null) return super.getServletPath();
        else return this.servletPath;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    @Override
    public String getRequestURI() {
        if(this.requestURI==null) return super.getRequestURI();
        else return this.requestURI;
    }

    public void setRequestBody(byte[] bytes){
        requestBody = bytes;
        parameterMap = initMap(requestBody,this);
    }

    class AmarsoftEnumeration<T> implements Enumeration {
        private  Iterator<T> it;
        public AmarsoftEnumeration(Iterator<T> it){
            this.it = it;
        }
        @Override
        public boolean hasMoreElements() {
            return it.hasNext();
        }
        @Override
        public Object nextElement() {
            return it.next();
        }
    }

}
