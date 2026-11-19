package com.kostly.controller;

import com.kostly.entity.InvoiceStatus;
import com.kostly.entity.Payment;

import com.kostly.repository.InvoiceRepository;
import com.kostly.repository.PaymentRepository;

import com.kostly.service.ReportService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final ReportService reportService;

    public ReportController(
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository,
            ReportService reportService
    ) {

        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.reportService = reportService;
    }

    // Export laporan PDF dan Excel
    @GetMapping("/export/{format}")
    public ResponseEntity<byte[]> export(

            @PathVariable String format,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate

    ) throws IOException {

        boolean excel =
                "excel".equalsIgnoreCase(format)
                || "xlsx".equalsIgnoreCase(format);

        boolean pdf = "pdf".equalsIgnoreCase(format);

        if (!excel && !pdf) {
            return ResponseEntity.badRequest().build();
        }

        byte[] content = reportService.export(
                startDate,
                endDate,
                excel ? "excel" : "pdf"
        );

        String filename = excel
                ? "laporan-penerimaan.xlsx"
                : "laporan-penerimaan.pdf";

        MediaType contentType = excel
                ? MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
                : MediaType.APPLICATION_PDF;

        return ResponseEntity.ok()

                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + "\""
                )

                .contentType(contentType)

                .body(content);
    }

    // Halaman laporan
    @GetMapping
    @Transactional(readOnly = true)
    public String index(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            Model model

    ) {

        LocalDate start = startDate;
        LocalDate end = endDate;

        if (start != null && end != null
                && end.isBefore(start)) {

            LocalDate temp = start;
            start = end;
            end = temp;
        }

        List<Payment> payments;

        if (start != null && end != null) {

            payments = paymentRepository
                    .findByPaymentDateBetweenOrderByPaymentDateDesc(
                            start, end);

        } else if (start != null) {

            payments = paymentRepository
                    .findByPaymentDateGreaterThanEqualOrderByPaymentDateDesc(
                            start);

        } else if (end != null) {

            payments = paymentRepository
                    .findByPaymentDateLessThanEqualOrderByPaymentDateDesc(
                            end);

        } else {

            payments = paymentRepository
                    .findAllByOrderByPaymentDateDesc();
        }

        BigDecimal totalRevenue = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("payments", payments);

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("paymentCount", payments.size());

        model.addAttribute(
                "unpaidCount",
                invoiceRepository.countByStatus(
                        InvoiceStatus.UNPAID
                )
        );

        return "reports/index";
    }
}
