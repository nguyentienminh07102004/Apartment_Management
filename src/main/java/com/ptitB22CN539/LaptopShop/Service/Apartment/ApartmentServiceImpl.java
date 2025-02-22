package com.ptitB22CN539.LaptopShop.Service.Apartment;

import com.ptitB22CN539.LaptopShop.DTO.Apartment.ApartmentRegisterRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.Apartment.ApartmentSearchRequestDTO;
import com.ptitB22CN539.LaptopShop.Domains.ApartmentEntity;
import com.ptitB22CN539.LaptopShop.Domains.ApartmentEntity_;
import com.ptitB22CN539.LaptopShop.Domains.ApartmentUserEntity;
import com.ptitB22CN539.LaptopShop.Domains.ApartmentUserEntity_;
import com.ptitB22CN539.LaptopShop.Domains.UserEntity;
import com.ptitB22CN539.LaptopShop.Domains.UserEntity_;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.DataInvalidException;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.ExceptionVariable;
import com.ptitB22CN539.LaptopShop.Mapper.Apartment.ApartmentMapper;
import com.ptitB22CN539.LaptopShop.Repository.ApartmentRepository;
import com.ptitB22CN539.LaptopShop.Service.User.IUserService;
import com.ptitB22CN539.LaptopShop.Utils.PageableUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentServiceImpl implements IApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final PageableUtils pageableUtils;
    private final ApartmentMapper apartmentMapper;
    private final IUserService userService;

    @Override
    @Transactional(readOnly = true)
    public PagedModel<ApartmentEntity> getAll(Integer page, Integer limit) {
        return new PagedModel<>(apartmentRepository.findAll(pageableUtils.getPageable(page, limit)));
    }

    @Override
    @Transactional(readOnly = true)
    public ApartmentEntity getById(String id) {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new DataInvalidException(ExceptionVariable.APARTMENT_NOT_FOUND));
    }

    @Override
    @Transactional
    public ApartmentEntity save(ApartmentRegisterRequestDTO apartment) {
        ApartmentEntity apartmentEntity = apartmentMapper.registerToEntity(apartment);
        return apartmentRepository.save(apartmentEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<ApartmentEntity> getAll(ApartmentSearchRequestDTO apartmentSearchRequestDTO) {
        Specification<ApartmentEntity> specification = (root, query, builder) -> {
            Predicate predicate = builder.and(builder.conjunction());
            if (StringUtils.hasText(apartmentSearchRequestDTO.getId())) {
                predicate = builder.and(builder.equal(root.get(ApartmentEntity_.ID), apartmentSearchRequestDTO.getId()));
            }
            if (StringUtils.hasText(apartmentSearchRequestDTO.getName())) {
                predicate = builder.and(builder.equal(root.get(ApartmentEntity_.NAME), apartmentSearchRequestDTO.getName()));
            }
            if (apartmentSearchRequestDTO.getAreaFrom() != null) {
                predicate = builder.and(builder.greaterThan(root.get(ApartmentEntity_.AREA), apartmentSearchRequestDTO.getAreaFrom()));
            }
            if (apartmentSearchRequestDTO.getAreaTo() != null) {
                predicate = builder.and(builder.lessThan(root.get(ApartmentEntity_.AREA), apartmentSearchRequestDTO.getAreaTo()));
            }
            if (apartmentSearchRequestDTO.getFloor() != null) {
                predicate = builder.and(builder.equal(root.get(ApartmentEntity_.FLOOR), apartmentSearchRequestDTO.getFloor()));
            }
            if (StringUtils.hasText(apartmentSearchRequestDTO.getOwnerId())) {
                predicate = builder.and(builder.equal(root.get(ApartmentEntity_.APARTMENT_USERS)
                        .get(ApartmentUserEntity_.USER).get(UserEntity_.ID), apartmentSearchRequestDTO.getOwnerId()));
            }
            if (StringUtils.hasText(apartmentSearchRequestDTO.getOwnerName())) {
                predicate = builder.and(builder.like(root.get(ApartmentEntity_.APARTMENT_USERS)
                        .get(ApartmentUserEntity_.USER).get(UserEntity_.FULL_NAME), String.join("%", apartmentSearchRequestDTO.getOwnerName(), "%")));
            }
            if (apartmentSearchRequestDTO.getStatus() != null) {
                predicate = builder.and(builder.equal(root.get(ApartmentEntity_.STATUS), apartmentSearchRequestDTO.getStatus()));
            }
            return predicate;
        };
        return new PagedModel<>(apartmentRepository.findAll(specification, pageableUtils.getPageable(apartmentSearchRequestDTO.getPage(), apartmentSearchRequestDTO.getLimit())));
    }

    @Override
    @Transactional
    public ApartmentEntity rentalApartment(String userId, String apartmentId, boolean isOwner) {
        ApartmentEntity apartment = this.getById(apartmentId);
        // chỉ có 1 owner
        List<ApartmentUserEntity> apartmentUserEntities = apartment.getApartmentUsers();
        if (isOwner) {
            for (ApartmentUserEntity apartmentUser : apartmentUserEntities) {
                if (apartmentUser.getIsOwner()) {
                    throw new DataInvalidException(ExceptionVariable.APARTMENT_ALREADY_HAS_OWNER);
                }

            }
        }
        UserEntity user = userService.getUserById(userId);
        ApartmentUserEntity apartmentUserEntity = new ApartmentUserEntity();
        apartmentUserEntity.setUser(user);
        apartmentUserEntity.setApartment(apartment);
        apartmentUserEntity.setIsOwner(isOwner);
        apartmentUserEntities.add(apartmentUserEntity);
        apartment.setApartmentUsers(apartmentUserEntities);
        return apartmentRepository.save(apartment);
    }
}
