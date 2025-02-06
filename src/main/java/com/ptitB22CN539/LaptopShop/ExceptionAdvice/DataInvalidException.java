package com.ptitB22CN539.LaptopShop.ExceptionAdvice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DataInvalidException extends RuntimeException {
    private ExceptionVariable exceptionVariable;
}
