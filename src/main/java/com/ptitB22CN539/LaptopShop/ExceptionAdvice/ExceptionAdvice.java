package com.ptitB22CN539.LaptopShop.ExceptionAdvice;

import com.ptitB22CN539.LaptopShop.DTO.APIResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(value = DataInvalidException.class)
    public APIResponse handleDataInvalidException(DataInvalidException exception) {
        return APIResponse.builder()
                .message(exception.getExceptionVariable().getMessage())
                .code(exception.getExceptionVariable().getStatus().value())
                .data(exception.getMessage())
                .build();
    }
}
