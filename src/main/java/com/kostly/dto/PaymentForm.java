package com.kostly.dto;

import com.kostly.entity.PaymentMethod;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentForm {

    @NotNull(message = "Tanggal pembayaran wajib diisi")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate paymentDate = LocalDate.now();

    @NotNull(message = "Jumlah pembayaran wajib diisi")
    @DecimalMin(value = "0.01", message = "Jumlah pembayaran harus lebih dari nol")
    private BigDecimal amount;

    @NotNull(message = "Metode pembayaran wajib dipilih")
    private PaymentMethod paymentMethod = PaymentMethod.TRANSFER;

    private String notes;

    public PaymentForm() {}

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
