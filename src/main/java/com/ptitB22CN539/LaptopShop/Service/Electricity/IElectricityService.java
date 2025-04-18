package com.ptitB22CN539.LaptopShop.Service.Electricity;

import com.ptitB22CN539.LaptopShop.DTO.Electricity.ElectricityRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.Electricity.ElectricitySearchRequestDTO;
import com.ptitB22CN539.LaptopShop.Domains.ElectricityFeeEntity;
import org.springframework.data.web.PagedModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IElectricityService {
    PagedModel<ElectricityFeeEntity> findAllElectricityFee(Integer page, Integer limit);
    PagedModel<ElectricityFeeEntity> findAllByApartment_Id(String apartmentId, Integer page, Integer limit);
    ElectricityFeeEntity findElectricityFeeById(String id);
    ElectricityFeeEntity save(ElectricityRequestDTO electricityRequestDTO);
    PagedModel<ElectricityFeeEntity> findAllElectricityFee(ElectricitySearchRequestDTO electricitySearchRequestDTO);
    Map<String, Double> getElectricityFeeChartByApartmentId(String paymentPeriod);
    Long countAllElectricityByPaymentPeriod(String paymentPeriod);
    List<ElectricityFeeEntity> saveFromFileExcel(MultipartFile file);
}