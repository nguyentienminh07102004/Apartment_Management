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
public class ApartmentSearchRequestDTO {
    private String id;
    private String name;
    private Integer floor;
    private Double areaFrom;
    private Double areaTo;
    private String ownerId;
    private String ownerName;
    private ApartmentStatus status;
    private Integer page;
    private Integer limit;
}
