package com.ptitB22CN539.LaptopShop.Service.Water;

import com.ptitB22CN539.LaptopShop.DTO.Water.WaterFeeRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.Water.WaterSearchRequestDTO;
import com.ptitB22CN539.LaptopShop.Domains.WaterFeeEntity;
import org.springframework.data.web.PagedModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IWaterService {
    PagedModel<WaterFeeEntity> findAllWaterFee(Integer page, Integer limit);
    PagedModel<WaterFeeEntity> findAllByApartment_Id(String apartmentId, Integer page, Integer limit);
    WaterFeeEntity findWaterFeeById(String id);
    WaterFeeEntity save(WaterFeeRequestDTO waterFeeRequestDTO);
    PagedModel<WaterFeeEntity> findAllWaterFee(WaterSearchRequestDTO waterSearchRequestDTO);
    Map<String, Double> getWaterFeeChartByApartmentId(String paymentPeriod);
    List<WaterFeeEntity> saveFromExcel(MultipartFile file);
    Long countByPaymentPeriod(String paymentPeriod);
}