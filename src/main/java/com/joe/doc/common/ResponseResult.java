package com.joe.doc.common;

import com.joe.doc.constant.ResponseMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Objects;

/**
 * spring mvc response result
 *
 * @author JoezBlackZ
 */
@Data
@Schema(name = "ResponseResult", description = "返回对象")
public class ResponseResult<T> {

    @Schema(description = "状态码")
    private int code;

    @Schema(description = "消息")
    private String msg;

    @Schema(description = "数据")
    private T data;

    @Schema(description = "时间戳")
    private long timestamp = System.currentTimeMillis();

    private ResponseResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private ResponseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> ResponseResult<T> success(T data, String message) {
        return new ResponseResult<>(200, message, data);
    }

    public static <T> ResponseResult<T> success(T data, ResponseMessage message) {
        return new ResponseResult<>(200, message.getMessage(), data);
    }

    public static <T> ResponseResult<T> fail(String message) {
        return new ResponseResult<>(500, message);
    }

    public static <T> ResponseResult<T> fail(ResponseMessage message) {
        return new ResponseResult<>(500, message.getMessage());
    }

    public ResponseResult<T> data(T object) {
        this.data = object;
        return this;
    }

    public boolean ok() {
        return Objects.equals(this.code, 200);
    }

}
