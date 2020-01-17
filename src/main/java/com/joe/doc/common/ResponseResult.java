package com.joe.doc.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * spring mvc response result
 *
 * @author JoezBlackZ
 */
@ApiModel(value = "response result", description = "This is doc-search web mvc response result, you cam see the detail of every parameter.")
@Data
public class ResponseResult {

    @ApiModelProperty(name = "code", notes = "response code of request.")
    private int code;

    @ApiModelProperty(name = "msg", notes = "response message of request.")
    private String msg;

    @ApiModelProperty(name = "data", notes = "response data of request.")
    private Object data;

    @ApiModelProperty(name = "count", notes = "response data count of request, eg: records count.")
    private long count;

    @ApiModelProperty(name = "expandedResults", notes = "response expanded param of request.")
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
