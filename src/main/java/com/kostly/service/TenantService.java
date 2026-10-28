package com.kostly.service;

import com.kostly.entity.Tenant;
import com.kostly.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Transactional(readOnly = true)
    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Tenant findById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Data penghuni tidak ditemukan"));
    }

    public Tenant save(Tenant tenant) {
        boolean duplicate;

        if (tenant.getId() == null) {
            duplicate = tenantRepository.existsByNik(
                    tenant.getNik());
        } else {
            duplicate = tenantRepository.existsByNikAndIdNot(
                    tenant.getNik(), tenant.getId());
        }

        if (duplicate) {
            throw new IllegalArgumentException(
                    "NIK sudah terdaftar");
        }

        return tenantRepository.save(tenant);
    }

    public void delete(Long id) {
        tenantRepository.delete(findById(id));
    }
}