package com.kostly.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ContractForm {

    @NotNull(message = "Penyewa wajib dipilih")
    private Long tenantId;

    @NotNull(message = "Kamar wajib dipilih")
    private Long roomId;

    @NotNull(message = "Tanggal mulai wajib diisi")
    private LocalDate startDate;

    @NotNull(message = "Tanggal akhir wajib diisi")
    private LocalDate endDate;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}