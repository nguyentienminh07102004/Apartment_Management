package com.ptitB22CN539.LaptopShop.DTO.Apartment;

import com.ptitB22CN539.LaptopShop.Config.ApartmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApartmentRegisterRequestDTO {
    private String name;
    private String phone;
    private Double area;
    private Integer floor;
    private ApartmentStatus status;
}
