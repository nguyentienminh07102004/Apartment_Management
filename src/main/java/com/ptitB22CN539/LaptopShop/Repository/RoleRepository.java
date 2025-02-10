package com.ptitB22CN539.LaptopShop.Repository;

import com.ptitB22CN539.LaptopShop.Domains.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, String> {
    RoleEntity findByName(String name);
}
