package com.joe.doc.exception;

/**
 * @author zhangqi
 */
public class FileParseException extends RuntimeException {
    public FileParseException() {
        super();
    }

    public FileParseException(String message) {
        super(message);
    }

    public FileParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileParseException(Throwable cause) {
        super(cause);
    }

}
