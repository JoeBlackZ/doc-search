package com.joe.doc.component;

import com.joe.doc.common.ResponseResult;
import com.joe.doc.exception.FileParseException;
import com.joe.doc.exception.SearchException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author zhangqi
 */
@RestControllerAdvice
public class GlobalExceptionControllerHandler {

    /**
     * 拦截所有程序异常
     *
     * @param exception Exception
     * @return 错误处理结果
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseResult<Object> errorHandler(Exception exception) {
        return ResponseResult.fail(exception.getMessage());
    }

    /**
     * SearchException 异常
     *
     * @param exception SearchException
     * @return 错误处理结果
     */
    @ExceptionHandler(value = SearchException.class)
    public ResponseResult<Object> searchErrorHandler(SearchException exception) {
        return ResponseResult.fail(exception.getMessage());
    }

    /**
     * FileParseException 异常
     *
     * @param exception FileParseException
     * @return 错误处理结果
     */
    @ExceptionHandler(value = FileParseException.class)
    public ResponseResult<Object> parserErrorHandler(FileParseException exception) {
        return ResponseResult.fail(exception.getMessage());
    }


}
