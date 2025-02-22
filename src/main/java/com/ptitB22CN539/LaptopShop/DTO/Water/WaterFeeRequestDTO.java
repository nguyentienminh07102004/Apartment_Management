package com.ptitB22CN539.LaptopShop.DTO.Water;

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
public class WaterFeeRequestDTO {
    private Date fromDate;
    private Date toDate;
    private Integer waterIndexStart;
    private Integer waterIndexEnd;
    private Date dueDate;
    private String apartmentId;
    private Double priceUnit;
    private ServiceFeeStatus status;
}
