package com.joe.doc.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

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

    private long count;

    private Map<String, Object> expandedResults;

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

    public ResponseResult count(long count) {
        this.count = count;
        return this;
    }

    public ResponseResult otherData(String key, Object value) {
        if (this.expandedResults == null) {
            this.expandedResults = new HashMap<>(16);
        }
        this.expandedResults.put(key, value);
        return this;
    }

}
