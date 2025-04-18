package com.ptitB22CN539.LaptopShop.ExceptionAdvice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
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
    OLD_PASSWORD_NEW_PASSWORD_MATCH(400, "Old password and new password is match", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_FOUND(400, "Permission is not exists", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOGIN_MAX_DEVICE(400, "Account login max device", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),
    APARTMENT_NOT_FOUND(400, "Apartment is not exists", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(400, "User Id is not exists", HttpStatus.BAD_REQUEST),
    USER_LOCKED(400, "User locked", HttpStatus.BAD_REQUEST),
    APARTMENT_ALREADY_HAS_OWNER(400, "Apartment already has an owner", HttpStatus.BAD_REQUEST),
    WATER_FEE_NOT_FOUND(400, "Water fee is not exists", HttpStatus.BAD_REQUEST),
    ELECTRICITY_FEE_NOT_FOUND(400, "Electricity fee is not exists", HttpStatus.BAD_REQUEST),
    FILE_EMPTY(400, "file is empty", HttpStatus.BAD_REQUEST),
    FILE_FORMAT_NOT_SUPPORTED(400, "File format is not supported", HttpStatus.BAD_REQUEST),
    FILE_EXCEL_NAME_INVALID(400, "File excel name is invalid", HttpStatus.BAD_REQUEST),
    FILE_HAS_IMPORTED(400, "File has imported", HttpStatus.BAD_REQUEST),
    ;
    private final Integer code;
    private final String message;
    private final HttpStatus status;
}
