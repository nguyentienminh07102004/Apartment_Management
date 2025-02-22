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
public class WaterSearchRequestDTO {
    private String id;
    private String apartmentId;
    private Date fromDate;
    private Date toDate;
    private ServiceFeeStatus status;
    private Integer page;
    private Integer limit;
}
