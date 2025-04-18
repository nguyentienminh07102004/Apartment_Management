package com.ptitB22CN539.LaptopShop.ExceptionAdvice;

import com.ptitB22CN539.LaptopShop.DTO.APIResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(value = DataInvalidException.class)
    public ResponseEntity<APIResponse> handleDataInvalidException(DataInvalidException exception) {
        APIResponse response = APIResponse.builder()
                .message(exception.getExceptionVariable().getMessage())
                .code(exception.getExceptionVariable().getStatus().value())
                .data(exception.getMessage())
                .build();
        return ResponseEntity.status(exception.getExceptionVariable().getStatus()).body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ExceptionVariable exceptionVariable = ExceptionVariable.valueOf(Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage());
        APIResponse response = APIResponse.builder()
                .message(exceptionVariable.getMessage())
                .code(exceptionVariable.getStatus().value())
                .data(exceptionVariable.getMessage())
                .build();
        return ResponseEntity.status(exceptionVariable.getStatus()).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIResponse> handleConstraintViolation(ConstraintViolationException exception) {
        ExceptionVariable exceptionVariable = ExceptionVariable.valueOf(exception.getMessage());
        APIResponse response = APIResponse.builder()
                .message(exceptionVariable.getMessage())
                .code(exceptionVariable.getStatus().value())
                .data(exceptionVariable.getMessage())
                .build();
        return ResponseEntity.status(exceptionVariable.getStatus()).body(response);
    }
}
