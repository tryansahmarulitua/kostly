package com.kostly.controller;

import com.kostly.entity.Invoice;
import com.kostly.entity.InvoiceStatus;
import com.kostly.entity.Payment;
import com.kostly.repository.InvoiceRepository;
import com.kostly.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private ReportController reportController;

    @Test
    void testFinancialReportCalculationAndSwapDates() {
        LocalDate start = LocalDate.of(2026, 11, 30);
        LocalDate end = LocalDate.of(2026, 11, 1);

        Payment payment1 = new Payment();
        payment1.setAmount(BigDecimal.valueOf(1500000));
        payment1.setPaymentDate(LocalDate.of(2026, 11, 5));

        Payment payment2 = new Payment();
        payment2.setAmount(BigDecimal.valueOf(2000000));
        payment2.setPaymentDate(LocalDate.of(2026, 11, 10));

        when(paymentRepository.findByPaymentDateBetweenOrderByPaymentDateDesc(end, start))
                .thenReturn(List.of(payment1, payment2));
        when(invoiceRepository.countByStatus(InvoiceStatus.UNPAID)).thenReturn(3L);

        Model model = new ExtendedModelMap();
        String view = reportController.index(start, end, model);

        assertEquals("reports/index", view);
        assertEquals(end, model.getAttribute("startDate"));
        assertEquals(start, model.getAttribute("endDate"));
        assertEquals(BigDecimal.valueOf(3500000), model.getAttribute("totalRevenue"));
        assertEquals(2, model.getAttribute("paymentCount"));
        assertEquals(3L, model.getAttribute("unpaidCount"));
    }

    @Test
    void testFinancialReportWithoutDateFilterReturnsAllPayments() {
        Payment payment = new Payment();
        payment.setAmount(BigDecimal.valueOf(2000000));
        payment.setPaymentDate(LocalDate.of(2026, 10, 29));

        when(paymentRepository.findAllByOrderByPaymentDateDesc()).thenReturn(List.of(payment));
        when(invoiceRepository.countByStatus(InvoiceStatus.UNPAID)).thenReturn(1L);

        Model model = new ExtendedModelMap();
        String view = reportController.index(null, null, model);

        assertEquals("reports/index", view);
        assertEquals(null, model.getAttribute("startDate"));
        assertEquals(null, model.getAttribute("endDate"));
        assertEquals(BigDecimal.valueOf(2000000), model.getAttribute("totalRevenue"));
        assertEquals(1, model.getAttribute("paymentCount"));
        assertEquals(1L, model.getAttribute("unpaidCount"));
    }
}
