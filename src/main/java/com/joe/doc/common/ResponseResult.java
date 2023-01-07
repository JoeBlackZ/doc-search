package com.joe.doc.common;

import lombok.Data;

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
        return new ResponseResult(0);
    }

    public static ResponseResult fail() {
        return new ResponseResult(1);
    }

    public ResponseResult msg(Object object) {
        this.msg = object.toString();
        return this;
    }

    public ResponseResult data(Object object) {
        this.data = object;
        return this;
    }

}
