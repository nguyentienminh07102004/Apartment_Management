package com.ptitB22CN539.LaptopShop.DTO.User;

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
public class ResidentRequestDTO {
    private String id;
    private String name;
    private String apartmentId;
    private Integer page;
    private Integer limit;
}
