package com.kostly.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rental_contracts")
public class RentalContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @NotNull(message = "Tanggal mulai wajib diisi")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Tanggal akhir wajib diisi")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Harga sewa wajib diisi")
    @DecimalMin(value = "0.01", message = "Harga harus lebih dari nol")
    @Column(name = "monthly_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal monthlyPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractStatus status = ContractStatus.ACTIVE;

    public RentalContract() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public ContractStatus getStatus() { return status; }
    public void setStatus(ContractStatus status) {
        this.status = status;
    }
}