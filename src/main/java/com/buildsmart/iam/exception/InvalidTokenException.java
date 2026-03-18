package com.buildsmart.iam.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception thrown when a JWT token is missing, malformed or invalid.
 */
public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException(String msg) {
        super(msg);
    }

    public InvalidTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

