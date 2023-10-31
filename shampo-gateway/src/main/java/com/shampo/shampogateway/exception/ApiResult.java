package com.shampo.shampogateway.exception;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * ClassName:ApiResult
 * Package:com.shampo.shampogateway.exception
 * Description:
 *
 * @Author kkli
 * @Create 2023/10/31 21:15
 * #Version 1.1
 */
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 1166356696537391753L;

    private Integer code;

    private String msg;

    private T data;

    public ApiResult() {
    }

    public ApiResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ApiResult success() {
        return new ApiResult(HttpStatus.OK.value(), "success", null);
    }

    public static ApiResult success(String msg) {
        return new ApiResult(HttpStatus.OK.value(), msg, null);
    }

    public static ApiResult success(String msg, Object data) {
        return new ApiResult(HttpStatus.OK.value(), msg, data);
    }

    public static ApiResult error(HttpStatus status, String msg) {
        return new ApiResult(status.value(), msg, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
