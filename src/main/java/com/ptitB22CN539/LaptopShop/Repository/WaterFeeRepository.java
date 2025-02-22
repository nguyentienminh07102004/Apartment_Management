package com.ptitB22CN539.LaptopShop.Repository;

import com.ptitB22CN539.LaptopShop.Domains.WaterFeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WaterFeeRepository extends JpaRepository<WaterFeeEntity, String>, JpaSpecificationExecutor<WaterFeeEntity> {
    Page<WaterFeeEntity> findByApartment_Id(String apartmentId, Pageable pageable);
}
