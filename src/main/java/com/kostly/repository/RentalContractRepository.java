package com.kostly.repository;

import com.kostly.entity.ContractStatus;
import com.kostly.entity.RentalContract;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RentalContractRepository
        extends JpaRepository<RentalContract, Long> {

    List<RentalContract> findByStatus(ContractStatus status);

    boolean existsByRoomIdAndStatus(
            Long roomId, ContractStatus status);

    boolean existsByTenantIdAndStatus(
            Long tenantId, ContractStatus status);
}