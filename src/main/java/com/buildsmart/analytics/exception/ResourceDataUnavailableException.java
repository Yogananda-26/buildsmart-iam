package com.buildsmart.analytics.exception;

public class ResourceDataUnavailableException extends RuntimeException {

    public ResourceDataUnavailableException(String message) {
        super(message);
    }

    public ResourceDataUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
