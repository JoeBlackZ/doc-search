package com.joe.doc.common;

import lombok.Data;

import java.util.Objects;

/**
 * spring mvc response result
 *
 * @author JoezBlackZ
 */
@Data
public class ResponseResult {

    private int code;

    private String msg;

    private Object data;

    private long timestamp = System.currentTimeMillis();

    private ResponseResult(int code) {
        this.code = code;
    }

    public static ResponseResult success() {
        return new ResponseResult(200);
    }

    public static ResponseResult fail() {
        return new ResponseResult(500);
    }

    public ResponseResult msg(Object object) {
        this.msg = object.toString();
        return this;
    }

    public ResponseResult data(Object object) {
        this.data = object;
        return this;
    }

    public boolean ok() {
        return Objects.equals(this.code, 200);
    }

}
