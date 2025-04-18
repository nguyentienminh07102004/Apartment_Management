package com.ptitB22CN539.LaptopShop.DTO.Electricity;

import com.ptitB22CN539.LaptopShop.Config.ServiceFeeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectricityRequestDTO {
    private String id;
    private String paymentPeriod;
    private Integer electricityIndexStart;
    private Integer electricityIndexEnd;
    private Double priceUnit;
    private Date dueDate;
    private String apartmentId;
    private ServiceFeeStatus status;
}
