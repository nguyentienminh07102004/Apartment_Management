package com.ptitB22CN539.LaptopShop.Repository;

import com.ptitB22CN539.LaptopShop.Domains.JwtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface JwtRepository extends JpaRepository<JwtEntity, String> {
    void deleteByToken(String token);
    boolean existsByToken(String token);
    void deleteAllByExpiredDateBefore(Date date);
}
