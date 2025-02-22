package com.ptitB22CN539.LaptopShop.Service.Electricity;

import com.ptitB22CN539.LaptopShop.DTO.Electricity.ElectricityRequestDTO;
import com.ptitB22CN539.LaptopShop.Domains.ElectricityFeeEntity;
import org.springframework.data.web.PagedModel;

public interface IElectricityService {
    ElectricityFeeEntity save(ElectricityRequestDTO electricityRequestDTO);
    PagedModel<ElectricityFeeEntity> getAllElectricityFee(Integer page, Integer limit);
    PagedModel<ElectricityFeeEntity> getElectricityFeeByApartmentId(String apartmentId, Integer page, Integer limit);
    PagedModel<ElectricityFeeEntity> getElectricityFeeExpired(Integer page, Integer limit);
}
