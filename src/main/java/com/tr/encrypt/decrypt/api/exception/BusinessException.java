package com.tr.encrypt.decrypt.api.exception;

/**
 * @Author: TR
 */
public class BusinessException extends RuntimeException {

    private Integer code;

    public BusinessException(String string) {
        super(string);
        this.code = 0;
    }

    public BusinessException(Integer code, String string) {
        super(string);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
