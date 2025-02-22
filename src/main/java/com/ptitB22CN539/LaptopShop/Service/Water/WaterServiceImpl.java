package com.ptitB22CN539.LaptopShop.Service.Water;

import com.ptitB22CN539.LaptopShop.DTO.Water.WaterFeeRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.Water.WaterSearchRequestDTO;
import com.ptitB22CN539.LaptopShop.Domains.ApartmentEntity;
import com.ptitB22CN539.LaptopShop.Domains.ApartmentEntity_;
import com.ptitB22CN539.LaptopShop.Domains.WaterFeeEntity;
import com.ptitB22CN539.LaptopShop.Domains.WaterFeeEntity_;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.DataInvalidException;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.ExceptionVariable;
import com.ptitB22CN539.LaptopShop.Mapper.WaterMapper;
import com.ptitB22CN539.LaptopShop.Repository.WaterFeeRepository;
import com.ptitB22CN539.LaptopShop.Service.Apartment.IApartmentService;
import com.ptitB22CN539.LaptopShop.Utils.PageableUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WaterServiceImpl implements IWaterService {
    private final WaterFeeRepository waterFeeRepository;
    private final PageableUtils pageableUtils;
    private final IApartmentService apartmentService;
    private final WaterMapper waterMapper;

    @Override
    @Transactional(readOnly = true)
    public PagedModel<WaterFeeEntity> findAllWaterFee(Integer page, Integer limit) {
        return new PagedModel<>(waterFeeRepository.findAll(pageableUtils.getPageable(page, limit)));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<WaterFeeEntity> findAllByApartment_Id(String apartmentId, Integer page, Integer limit) {
        apartmentService.getById(apartmentId);
        return new PagedModel<>(waterFeeRepository.findByApartment_Id(apartmentId, pageableUtils.getPageable(page, limit)));
    }

    @Override
    @Transactional(readOnly = true)
    public WaterFeeEntity findWaterFeeById(String id) {
        return waterFeeRepository.findById(id)
                .orElseThrow(() -> new DataInvalidException(ExceptionVariable.WATER_FEE_NOT_FOUND));
    }

    @Override
    @Transactional
    public WaterFeeEntity save(WaterFeeRequestDTO waterFeeRequestDTO) {
        WaterFeeEntity waterFee = waterMapper.requestToEntity(waterFeeRequestDTO);
        ApartmentEntity apartment = apartmentService.getById(waterFeeRequestDTO.getApartmentId());
        waterFee.setApartment(apartment);
        return waterFeeRepository.save(waterFee);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<WaterFeeEntity> findAllWaterFee(WaterSearchRequestDTO waterSearchRequestDTO) {
        Specification<WaterFeeEntity> specification = (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            if (StringUtils.hasText(waterSearchRequestDTO.getApartmentId())) {
                predicate = builder.and(builder.like(root.get(WaterFeeEntity_.APARTMENT).get(ApartmentEntity_.ID),
                        String.join("%", waterSearchRequestDTO.getApartmentId(), "%")));
            }
            if (StringUtils.hasText(waterSearchRequestDTO.getId())) {
                predicate = builder.and(builder.like(root.get(WaterFeeEntity_.ID),
                        String.join("%", waterSearchRequestDTO.getId(), "%")));
            }
            if (waterSearchRequestDTO.getFromDate() != null) {
                predicate = builder.and(builder.greaterThanOrEqualTo(root.get(WaterFeeEntity_.FROM_DATE), waterSearchRequestDTO.getFromDate()));
            }
            if (waterSearchRequestDTO.getToDate() != null) {
                predicate = builder.and(builder.lessThanOrEqualTo(root.get(WaterFeeEntity_.TO_DATE), waterSearchRequestDTO.getToDate()));
            }
            if (waterSearchRequestDTO.getStatus() != null) {
                predicate = builder.and(builder.equal(root.get(WaterFeeEntity_.STATUS), waterSearchRequestDTO.getStatus()));
            }
            return predicate;
        };
        return new PagedModel<>(waterFeeRepository.findAll(specification,
                pageableUtils.getPageable(waterSearchRequestDTO.getPage(), waterSearchRequestDTO.getLimit())));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Double> getWaterFeeChartByApartmentId() {
        List<WaterFeeEntity> waterFeeEntities = waterFeeRepository.findAll();
        Map<String, Double> res = new HashMap<>();
        for (WaterFeeEntity waterFeeEntity : waterFeeEntities) {
            res.put(waterFeeEntity.getApartment().getName(), waterFeeEntity.getPriceUnit() * (waterFeeEntity.getWaterIndexEnd() - waterFeeEntity.getWaterIndexStart()));
        }
        return res;
    }
}
