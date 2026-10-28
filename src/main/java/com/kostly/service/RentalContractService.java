package com.kostly.service;

import com.kostly.entity.*;
import com.kostly.repository.RentalContractRepository;
import com.kostly.repository.RoomRepository;
import com.kostly.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RentalContractService {

    private final RentalContractRepository contractRepository;
    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;

    public RentalContractService(
            RentalContractRepository contractRepository,
            TenantRepository tenantRepository,
            RoomRepository roomRepository) {
        this.contractRepository = contractRepository;
        this.tenantRepository = tenantRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    public List<RentalContract> findAll() {
        return contractRepository.findAll();
    }

    public RentalContract create(RentalContract contract) {
        if (contract.getTenant() == null
                || contract.getTenant().getId() == null) {
            throw new IllegalArgumentException(
                    "Pilih penghuni terlebih dahulu.");
        }

        if (contract.getRoom() == null
                || contract.getRoom().getId() == null) {
            throw new IllegalArgumentException(
                    "Pilih kamar terlebih dahulu.");
        }

        Tenant tenant = tenantRepository
                .findById(contract.getTenant().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Penghuni tidak ditemukan."));

        Room room = roomRepository
                .findById(contract.getRoom().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Kamar tidak ditemukan."));

        if (contract.getStartDate() == null
                || contract.getEndDate() == null
                || contract.getEndDate().isBefore(
                        contract.getStartDate())) {
            throw new IllegalArgumentException(
                    "Tanggal akhir tidak boleh sebelum tanggal mulai.");
        }

        if (contract.getMonthlyPrice() == null
                || contract.getMonthlyPrice().signum() <= 0) {
            throw new IllegalArgumentException(
                    "Harga sewa harus lebih dari nol.");
        }

        if (room.getStatus() != RoomStatus.AVAILABLE
                || contractRepository.existsByRoomIdAndStatus(
                        room.getId(), ContractStatus.ACTIVE)) {
            throw new IllegalArgumentException(
                    "Kamar tidak tersedia untuk disewa.");
        }

        if (contractRepository.existsByTenantIdAndStatus(
                tenant.getId(), ContractStatus.ACTIVE)) {
            throw new IllegalArgumentException(
                    "Penghuni masih memiliki kontrak aktif.");
        }

        contract.setTenant(tenant);
        contract.setRoom(room);
        contract.setMonthlyPrice(room.getPrice());
        contract.setStatus(ContractStatus.ACTIVE);

        RentalContract saved = contractRepository.save(contract);

        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        return saved;
    }

    public void endContract(Long id) {
        RentalContract contract = contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Kontrak tidak ditemukan."));

        if (contract.getStatus() == ContractStatus.ENDED) {
            throw new IllegalArgumentException(
                    "Kontrak sudah diakhiri.");
        }

        contract.setStatus(ContractStatus.ENDED);
        contractRepository.save(contract);

        Room room = contract.getRoom();

        boolean hasOtherActiveContract =
                contractRepository.existsByRoomIdAndStatus(
                        room.getId(), ContractStatus.ACTIVE);

        if (!hasOtherActiveContract) {
            room.setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(room);
        }
    }
}