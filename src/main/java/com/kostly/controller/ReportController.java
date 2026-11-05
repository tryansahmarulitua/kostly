package com.kostly.controller;

import com.kostly.entity.InvoiceStatus;
import com.kostly.entity.Payment;
import com.kostly.repository.InvoiceRepository;
import com.kostly.repository.PaymentRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public ReportController(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        // ponytail: multi-currency and tax breakdown skipped, add when accounting module requested
        LocalDate start = (startDate != null) ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = (endDate != null) ? endDate : LocalDate.now();

        if (end.isBefore(start)) {
            LocalDate tmp = start;
            start = end;
            end = tmp;
        }

        List<Payment> payments = paymentRepository.findByPaymentDateBetweenOrderByPaymentDateDesc(start, end);
        BigDecimal totalRevenue = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("payments", payments);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("paymentCount", payments.size());
        model.addAttribute("unpaidCount", invoiceRepository.countByStatus(InvoiceStatus.UNPAID));

        return "reports/index";
    }
}
