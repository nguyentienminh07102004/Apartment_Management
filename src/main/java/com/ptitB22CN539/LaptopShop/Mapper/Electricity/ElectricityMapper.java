package com.ptitB22CN539.LaptopShop.Mapper.Electricity;

import com.ptitB22CN539.LaptopShop.DTO.Electricity.ElectricityRequestDTO;
import com.ptitB22CN539.LaptopShop.Domains.ElectricityFeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ElectricityMapper {
    @Mapping(target = "apartment", ignore = true)
    ElectricityFeeEntity requestToEntity(ElectricityRequestDTO electricityRequestDTO);
}
