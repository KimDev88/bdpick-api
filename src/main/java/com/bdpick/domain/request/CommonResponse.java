package com.bdpick.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommonResponse implements Serializable {
    private String code;
    private String message;
    private Object data;


    public CommonResponse setError() {
        code = ResponseCode.CODE_ERROR;
        message = ResponseCode.MESSAGE_ERROR;
        return this;
    }

    public CommonResponse setError(String message, Object data) {
        code = ResponseCode.CODE_ERROR;
        this.message = message;
        this.data = data;
        return this;
    }

    public CommonResponse setError(Object data) {
        setError();
        this.data = data;
        return this;
    }

    public CommonResponse() {
        this.code = ResponseCode.CODE_SUCCESS;
        this.message = ResponseCode.MESSAGE_SUCCESS;
    }

    public CommonResponse(Object data) {
        this();
        this.data = data;
    }

    public CommonResponse(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public CommonResponse setData(Object data) {
        this.data = data;
        return this;
    }

    public CommonResponse setCode(String code) {
        this.code = code;
        return this;
    }
}
