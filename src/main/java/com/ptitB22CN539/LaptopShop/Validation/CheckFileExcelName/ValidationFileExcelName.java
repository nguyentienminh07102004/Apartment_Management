package com.ptitB22CN539.LaptopShop.Validation.CheckFileExcelName;

import com.ptitB22CN539.LaptopShop.Config.FileExcelType;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.DataInvalidException;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.ExceptionVariable;
import com.ptitB22CN539.LaptopShop.Service.Electricity.IElectricityService;
import com.ptitB22CN539.LaptopShop.Service.Water.IWaterService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ValidationFileExcelName implements ConstraintValidator<CheckFileExcelName, MultipartFile> {
    private final IWaterService waterService;
    private final IElectricityService electricityService;
    private FileExcelType type;

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        String fileName = multipartFile.getOriginalFilename();
        if (fileName == null) {
            throw new DataInvalidException(ExceptionVariable.FILE_EXCEL_NAME_INVALID);
        }
        if (fileName.endsWith(".xlsx")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        } else if (fileName.endsWith(".xls")) {
            fileName = fileName.substring(0, fileName.length() - 3);
        } else {
            throw new DataInvalidException(ExceptionVariable.FILE_EXCEL_NAME_INVALID);
        }
        String[] fileNames = fileName.split("-");
        // Ex: Tiền điện tháng 2 năm 2025-02-2025.xlsx
        if (fileNames.length < 2) throw new DataInvalidException(ExceptionVariable.FILE_EXCEL_NAME_INVALID);
        String year = fileNames[fileNames.length - 1];
        year = year.substring(0, year.length() - 1);
        String month = fileNames[fileNames.length - 2];
        if (!NumberUtils.isDigits(month) || !NumberUtils.isDigits(year)) {
            throw new DataInvalidException(ExceptionVariable.FILE_EXCEL_NAME_INVALID);
        }
        String paymentPeriod = String.format("%02d/%d", Integer.parseInt(month), Integer.parseInt(year));
        if(type.equals(FileExcelType.ELECTRIC) && electricityService.countAllElectricityByPaymentPeriod(paymentPeriod) > 0) {
            throw new DataInvalidException(ExceptionVariable.FILE_HAS_IMPORTED);
        } else if (type.equals(FileExcelType.WATER) && waterService.countByPaymentPeriod(paymentPeriod) > 0) {
            throw new DataInvalidException(ExceptionVariable.FILE_HAS_IMPORTED);
        }
        return true;
    }

    @Override
    public void initialize(CheckFileExcelName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.type = constraintAnnotation.type();
    }
}
