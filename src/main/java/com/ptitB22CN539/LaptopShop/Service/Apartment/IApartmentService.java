package com.ptitB22CN539.LaptopShop.Service.Apartment;

import com.ptitB22CN539.LaptopShop.DTO.Apartment.ApartmentRegisterRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.Apartment.ApartmentSearchRequestDTO;
import com.ptitB22CN539.LaptopShop.Domains.ApartmentEntity;
import org.springframework.data.web.PagedModel;

public interface IApartmentService {
    PagedModel<ApartmentEntity> getAll(Integer page, Integer limit);
    ApartmentEntity getById(String id);
    ApartmentEntity save(ApartmentRegisterRequestDTO apartment);
    PagedModel<ApartmentEntity> getAll(ApartmentSearchRequestDTO apartmentSearchRequestDTO);
    ApartmentEntity rentalApartment(String userId, String apartmentId, boolean isOwner);
}
