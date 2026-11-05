package com.kostly.service;

import com.kostly.dto.InvoiceForm;
import com.kostly.dto.PaymentForm;
import com.kostly.entity.*;
import com.kostly.repository.InvoiceRepository;
import com.kostly.repository.PaymentRepository;
import com.kostly.repository.RentalContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RentalContractRepository contractRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private RentalContract contract;

    @BeforeEach
    void setUp() {
        contract = new RentalContract();
        contract.setId(1L);
        contract.setMonthlyPrice(BigDecimal.valueOf(1500000));
        contract.setStatus(ContractStatus.ACTIVE);
    }

    @Test
    void testCreateInvoice() {
        InvoiceForm form = new InvoiceForm();
        form.setContractId(1L);
        form.setAmount(BigDecimal.valueOf(1500000));
        form.setIssueDate(LocalDate.now());
        form.setDueDate(LocalDate.now().plusDays(7));
        form.setDescription("Sewa Kamar 101");

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(invoiceRepository.count()).thenReturn(0L);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        Invoice created = invoiceService.create(form);

        assertNotNull(created);
        assertEquals(InvoiceStatus.UNPAID, created.getStatus());
        assertEquals(BigDecimal.valueOf(1500000), created.getAmount());
        assertTrue(created.getInvoiceNumber().startsWith("INV-"));
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void testPayInvoice() {
        Invoice invoice = new Invoice();
        invoice.setId(10L);
        invoice.setAmount(BigDecimal.valueOf(1500000));
        invoice.setStatus(InvoiceStatus.UNPAID);

        PaymentForm form = new PaymentForm();
        form.setAmount(BigDecimal.valueOf(1500000));
        form.setPaymentDate(LocalDate.now());
        form.setPaymentMethod(PaymentMethod.TRANSFER);
        form.setNotes("Transfer BCA");

        when(invoiceRepository.findById(10L)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Payment payment = invoiceService.pay(10L, form);

        assertNotNull(payment);
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        assertEquals(PaymentMethod.TRANSFER, payment.getPaymentMethod());
        assertEquals(BigDecimal.valueOf(1500000), payment.getAmount());
        verify(invoiceRepository).save(invoice);
    }
}
