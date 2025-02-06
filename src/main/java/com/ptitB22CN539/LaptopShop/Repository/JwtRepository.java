package com.ptitB22CN539.LaptopShop.Repository;

import com.ptitB22CN539.LaptopShop.Domains.JwtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtRepository extends JpaRepository<JwtEntity, String> {
    void deleteByToken(String token);
}
