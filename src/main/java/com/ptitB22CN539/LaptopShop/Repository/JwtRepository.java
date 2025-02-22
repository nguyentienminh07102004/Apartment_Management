package com.ptitB22CN539.LaptopShop.Repository;

import com.ptitB22CN539.LaptopShop.Domains.JwtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface JwtRepository extends JpaRepository<JwtEntity, String> {
    List<JwtEntity> findByUser_Email(String email);
    Optional<JwtEntity> findByRefreshToken(String refreshToken);

    void deleteAllByRefreshTokenExpiatedDateBefore(Date refreshTokenExpiatedDateBefore);
}
