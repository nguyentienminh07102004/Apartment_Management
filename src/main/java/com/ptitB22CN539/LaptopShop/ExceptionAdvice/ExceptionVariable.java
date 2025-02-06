package com.ptitB22CN539.LaptopShop.ExceptionAdvice;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionVariable {
    EMAIL_NOT_FOUND(400, "Email is not exists", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTS(400, "Email already exists", HttpStatus.CONFLICT),
    EMAIL_INVALID(400, "Email is invalid", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(400, "Role is not exists", HttpStatus.BAD_REQUEST),
    FULL_NAME_NOT_NULL_OR_EMPTY(400, "Full name is not null or empty", HttpStatus.BAD_REQUEST),
    EMAIL_PASSWORD_NOT_CORRECT(400, "Email or password is not correct", HttpStatus.BAD_REQUEST),
    SERVER_ERROR(500, "Server error", HttpStatus.INTERNAL_SERVER_ERROR),
    TOKEN_INVALID(400, "Token is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_CONFIRM_PASSWORD_NOT_MATCH(400, "Password confirmation password is not match", HttpStatus.BAD_REQUEST),
    PASSWORD_LENGTH_NOT_CORRECT(400, "Password length is not correct", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_FOUND(400, "Permission is not exists", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOGIN_MAX_DEVICE(400, "Account login max device", HttpStatus.BAD_REQUEST),
    ;
    private final Integer code;
    private final String message;
    private final HttpStatus status;

    ExceptionVariable(Integer code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
