package com.ptitB22CN539.LaptopShop.Repository;

import com.ptitB22CN539.LaptopShop.Domains.ElectricityFeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectricityFeeRepository extends JpaRepository<ElectricityFeeEntity, String>, JpaSpecificationExecutor<ElectricityFeeEntity> {
    Page<ElectricityFeeEntity> findByApartment_Id(String apartmentId, Pageable pageable);
    List<ElectricityFeeEntity> findByPaymentPeriod(String paymentPeriod);
    Long countByPaymentPeriod(String paymentPeriod);
}
