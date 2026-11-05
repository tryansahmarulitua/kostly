package com.kostly.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceForm {

    @NotNull(message = "Kontrak wajib dipilih")
    private Long contractId;

    @NotNull(message = "Jumlah tagihan wajib diisi")
    @DecimalMin(value = "0.01", message = "Jumlah tagihan harus lebih dari nol")
    private BigDecimal amount;

    @NotNull(message = "Tanggal tagihan wajib diisi")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate issueDate = LocalDate.now();

    @NotNull(message = "Tanggal jatuh tempo wajib diisi")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate = LocalDate.now().plusDays(7);

    @NotBlank(message = "Keterangan wajib diisi")
    private String description;

    public InvoiceForm() {}

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
