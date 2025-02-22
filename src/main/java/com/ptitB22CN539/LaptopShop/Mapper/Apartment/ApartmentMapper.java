package com.ptitB22CN539.LaptopShop.Mapper.Apartment;

import com.ptitB22CN539.LaptopShop.DTO.Apartment.ApartmentRegisterRequestDTO;
import com.ptitB22CN539.LaptopShop.Domains.ApartmentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApartmentMapper {
    ApartmentEntity registerToEntity(ApartmentRegisterRequestDTO apartmentRequestDTO);
}
