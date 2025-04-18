package com.ptitB22CN539.LaptopShop.Service.Electricity;

import com.ptitB22CN539.LaptopShop.DTO.Electricity.ElectricityRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.Electricity.ElectricitySearchRequestDTO;
import com.ptitB22CN539.LaptopShop.Domains.ApartmentEntity;
import com.ptitB22CN539.LaptopShop.Domains.ApartmentEntity_;
import com.ptitB22CN539.LaptopShop.Domains.ElectricityFeeEntity;
import com.ptitB22CN539.LaptopShop.Domains.ElectricityFeeEntity_;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.DataInvalidException;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.ExceptionVariable;
import com.ptitB22CN539.LaptopShop.Mapper.Electricity.ElectricityMapper;
import com.ptitB22CN539.LaptopShop.Repository.ApartmentRepository;
import com.ptitB22CN539.LaptopShop.Repository.ElectricityFeeRepository;
import com.ptitB22CN539.LaptopShop.Utils.PageableUtils;
import com.ptitB22CN539.LaptopShop.Utils.ReadExcel;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Transactional(readOnly = true)
    public PagedModel<ElectricityFeeEntity> findAllElectricityFee(ElectricitySearchRequestDTO electricitySearchRequestDTO) {
        Specification<ElectricityFeeEntity> specification = (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            if (StringUtils.hasText(electricitySearchRequestDTO.getApartmentId())) {
                predicate = builder.and(builder.like(root.get(ElectricityFeeEntity_.APARTMENT).get(ApartmentEntity_.ID),
                        String.join("%", electricitySearchRequestDTO.getApartmentId(), "%")));
            }
            if (StringUtils.hasText(electricitySearchRequestDTO.getId())) {
                predicate = builder.and(builder.like(root.get(ElectricityFeeEntity_.ID),
                        String.join("%", electricitySearchRequestDTO.getId(), "%")));
            }
            if (StringUtils.hasText(electricitySearchRequestDTO.getPaymentPeriod())) {
                predicate = builder.and(builder.equal(root.get(ElectricityFeeEntity_.PAYMENT_PERIOD), electricitySearchRequestDTO.getPaymentPeriod()));
            }
            if (electricitySearchRequestDTO.getStatus() != null) {
                predicate = builder.and(builder.equal(root.get(ElectricityFeeEntity_.STATUS), electricitySearchRequestDTO.getStatus()));
            }
            return predicate;
        };
        return new PagedModel<>(electricityFeeRepository.findAll(specification,
                pageableUtils.getPageable(electricitySearchRequestDTO.getPage(), electricitySearchRequestDTO.getLimit())));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getElectricityFeeChartByApartmentId(String paymentPeriod) {
        List<ElectricityFeeEntity> electricityFeeEntities = electricityFeeRepository.findByPaymentPeriod(paymentPeriod);
        Map<String, Double> res = new HashMap<>();
        for (ElectricityFeeEntity electricityFeeEntity : electricityFeeEntities) {
            res.put(electricityFeeEntity.getApartment().getName(), electricityFeeEntity.getPriceUnit() * (electricityFeeEntity.getElectricityIndexEnd() - electricityFeeEntity.getElectricityIndexStart()));
        }
        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAllElectricityByPaymentPeriod(String paymentPeriod) {
        return electricityFeeRepository.countByPaymentPeriod(paymentPeriod);
    }

    @Override
    @Transactional
    public List<ElectricityFeeEntity> saveFromFileExcel(MultipartFile file) {
        ReadExcel<ElectricityRequestDTO> readExcel = new ReadExcel<>();
        List<ElectricityRequestDTO> listData = readExcel.readExcel(file, 0, ElectricityRequestDTO.class);
        List<ElectricityFeeEntity> res = new ArrayList<>();
        for (ElectricityRequestDTO electricityRequestDTO : listData) {
            res.add(this.save(electricityRequestDTO));
        }
        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<ElectricityFeeEntity> findAllElectricityFee(Integer page, Integer limit) {
        return new PagedModel<>(electricityFeeRepository.findAll(pageableUtils.getPageable(page, limit)));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<ElectricityFeeEntity> findAllByApartment_Id(String apartmentId, Integer page, Integer limit) {
        return new PagedModel<>(electricityFeeRepository.findByApartment_Id(apartmentId, pageableUtils.getPageable(page, limit)));
    }

    @Override
    @Transactional(readOnly = true)
    public ElectricityFeeEntity findElectricityFeeById(String id) {
        return electricityFeeRepository.findById(id)
                .orElseThrow(() -> new DataInvalidException(ExceptionVariable.ELECTRICITY_FEE_NOT_FOUND));
    }

}
