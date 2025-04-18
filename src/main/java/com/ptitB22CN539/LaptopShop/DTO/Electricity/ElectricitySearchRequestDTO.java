package com.ptitB22CN539.LaptopShop.DTO.Electricity;

import com.ptitB22CN539.LaptopShop.Config.ServiceFeeStatus;
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
public class ElectricitySearchRequestDTO {
    private String id;
    private String apartmentId;
    private String paymentPeriod;
    private ServiceFeeStatus status;
    private Integer page;
    private Integer limit;
}
