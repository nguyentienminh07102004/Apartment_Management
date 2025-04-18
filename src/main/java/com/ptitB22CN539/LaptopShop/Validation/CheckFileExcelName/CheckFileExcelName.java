package com.ptitB22CN539.LaptopShop.Validation.CheckFileExcelName;

import com.ptitB22CN539.LaptopShop.Config.FileExcelType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = {ValidationFileExcelName.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckFileExcelName {
    String message() default "FILE_EXCEL_NAME_INVALID";

    FileExcelType type();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
