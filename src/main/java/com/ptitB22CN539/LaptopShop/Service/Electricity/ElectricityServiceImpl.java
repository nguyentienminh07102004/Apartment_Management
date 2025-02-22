package com.ptitB22CN539.LaptopShop.Service.Electricity;

import com.ptitB22CN539.LaptopShop.DTO.Electricity.ElectricityRequestDTO;
import com.ptitB22CN539.LaptopShop.Domains.ApartmentEntity;
import com.ptitB22CN539.LaptopShop.Domains.ElectricityFeeEntity;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.DataInvalidException;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.ExceptionVariable;
import com.ptitB22CN539.LaptopShop.Mapper.Electricity.ElectricityMapper;
import com.ptitB22CN539.LaptopShop.Repository.ApartmentRepository;
import com.ptitB22CN539.LaptopShop.Repository.ElectricityFeeRepository;
import com.ptitB22CN539.LaptopShop.Utils.PageableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ElectricityServiceImpl implements IElectricityService {
    private final ElectricityFeeRepository electricityFeeRepository;
    private final PageableUtils pageableUtils;
    private final ElectricityMapper electricityMapper;
    private final ApartmentRepository apartmentRepository;

    @Override
    @Transactional
    public ElectricityFeeEntity save(ElectricityRequestDTO electricityRequestDTO) {
        ElectricityFeeEntity electricityFeeEntity = electricityMapper.requestToEntity(electricityRequestDTO);
        ApartmentEntity apartment = apartmentRepository.findById(electricityRequestDTO.getApartmentId())
                        .orElseThrow(() -> new DataInvalidException(ExceptionVariable.APARTMENT_NOT_FOUND));
        electricityFeeEntity.setApartment(apartment);
        return electricityFeeRepository.save(electricityFeeEntity);
    }

    @Override
    public PagedModel<ElectricityFeeEntity> getAllElectricityFee(Integer page, Integer limit) {
        return new PagedModel<>(electricityFeeRepository.findAll(pageableUtils.getPageable(page, limit)));
    }

    @Override
    public PagedModel<ElectricityFeeEntity> getElectricityFeeByApartmentId(String apartmentId, Integer page, Integer limit) {
        return new PagedModel<>(electricityFeeRepository.findByApartment_Id(apartmentId, pageableUtils.getPageable(page, limit)));
    }

    @Override
    public PagedModel<ElectricityFeeEntity> getElectricityFeeExpired(Integer page, Integer limit) {
        return new PagedModel<>(electricityFeeRepository.findByDueDateBefore(new Date(System.currentTimeMillis()), pageableUtils.getPageable(page, limit)));
    }


}
