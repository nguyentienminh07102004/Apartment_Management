package com.ptitB22CN539.LaptopShop.Mapper;

import com.ptitB22CN539.LaptopShop.DTO.Water.WaterFeeRequestDTO;
import com.ptitB22CN539.LaptopShop.Domains.WaterFeeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WaterMapper {
    WaterFeeEntity requestToEntity(WaterFeeRequestDTO waterFeeRequestDTO);
}
