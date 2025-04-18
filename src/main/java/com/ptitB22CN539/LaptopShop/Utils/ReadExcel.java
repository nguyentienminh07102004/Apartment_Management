package com.ptitB22CN539.LaptopShop.Utils;

import com.ptitB22CN539.LaptopShop.ExceptionAdvice.DataInvalidException;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.ExceptionVariable;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReadExcel<T> {
    public List<T> readExcel(MultipartFile file, Integer sheetIndex, Class<T> cla) {
        try {
            if (file.isEmpty()) {
                throw new DataInvalidException(ExceptionVariable.FILE_EMPTY);
            }
            InputStream inputStream = file.getInputStream();
            Workbook workbook;
            if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (file.getOriginalFilename().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                throw new DataInvalidException(ExceptionVariable.FILE_FORMAT_NOT_SUPPORTED);
            }
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            Map<Integer, String> mapIndexToName = new HashMap<>();
            List<T> result = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String cellName = String.join("", cell.getStringCellValue().strip()
                                .toLowerCase()
                                .split("\\s+"));
                        mapIndexToName.put(cell.getColumnIndex(), cellName);
                    }
                    continue;
                }
                Iterator<Cell> cellIterator = row.cellIterator();
                T t = cla.getDeclaredConstructor().newInstance();
                Map<String, Object> mapNameToValue = new HashMap<>();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    Object data = null;
                    switch (cell.getCellType()) {
                        case BOOLEAN:
                            data = cell.getBooleanCellValue();
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                data = cell.getDateCellValue();
                            } else {
                                data = cell.getNumericCellValue();
                            }
                            break;
                        case STRING:
                            data = cell.getStringCellValue();
                            break;
                        case FORMULA:
                            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                            CellValue cellValue = formulaEvaluator.evaluate(cell);
                            data = switch (cellValue.getCellType()) {
                                case STRING -> cellValue.getStringValue();
                                case NUMERIC -> cellValue.getNumberValue();
                                case BOOLEAN -> cell.getBooleanCellValue();
                                default -> null;
                            };
                            break;
                        default:
                            break;
                    }
                    Integer index = cell.getColumnIndex();
                    String columnName = mapIndexToName.get(index);
                    mapNameToValue.put(columnName, data);
                }
                setValue(cla, mapNameToValue, t);
                result.add(t);
            }
            return result;
        } catch (
                IOException | InvocationTargetException | InstantiationException | IllegalAccessException |
                NoSuchMethodException e) {
            throw new DataInvalidException(ExceptionVariable.FILE_FORMAT_NOT_SUPPORTED);
        }
    }

    private void setValue(Class<T> cla, Map<String, Object> values, T t) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        for (Field field : cla.getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName().strip().toLowerCase();
            Object value = values.get(name);
            if (field.getType().isEnum()) {
                field.set(t, Enum.valueOf((Class<Enum>) field.getType(), String.valueOf(value)));
            } else if (field.getType().equals(Integer.class)) {
                field.set(t, Double.valueOf(value.toString()).intValue());
            } else {
                field.set(t, field.getType().cast(value));
            }
        }
    }
}
