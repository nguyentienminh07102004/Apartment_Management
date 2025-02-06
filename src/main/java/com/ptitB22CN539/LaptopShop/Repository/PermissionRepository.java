package com.ptitB22CN539.LaptopShop.Repository;

import com.ptitB22CN539.LaptopShop.Domains.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, String> {
}
