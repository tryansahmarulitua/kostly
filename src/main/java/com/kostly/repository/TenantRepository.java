package com.kostly.repository;

import com.kostly.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository
        extends JpaRepository<Tenant, Long> {

    boolean existsByNik(String nik);

    boolean existsByNikAndIdNot(String nik, Long id);
}