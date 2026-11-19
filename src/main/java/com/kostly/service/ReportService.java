package com.kostly.service;

import com.kostly.entity.Payment;
import com.kostly.repository.PaymentRepository;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.*;

@Service
public class ReportService {

    private final PaymentRepository paymentRepository;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ReportService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public byte[] export(
            LocalDate start,
            LocalDate end,
            String format
    ) throws IOException {

        // Mengambil data pembayaran
        List<Payment> payments;

        if (start != null && end != null && end.isBefore(start)) {
            LocalDate temp = start;
            start = end;
            end = temp;
        }

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

        // Menyiapkan data untuk JasperReports
        List<Map<String, ?>> rows = new ArrayList<>();

        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Payment payment : payments) {

            Map<String, Object> row = new HashMap<>();

            String invoiceNumber = "";
            String tenantName = "";
            String roomNumber = "";

            if (payment.getInvoice() != null) {

                invoiceNumber = payment.getInvoice()
                        .getInvoiceNumber();

                if (payment.getInvoice().getContract() != null) {

                    tenantName = payment.getInvoice()
                            .getContract()
                            .getTenant()
                            .getFullName();

                    roomNumber = payment.getInvoice()
                            .getContract()
                            .getRoom()
                            .getRoomNumber();
                }
            }

            row.put("paymentDate",
                    payment.getPaymentDate() == null
                    ? ""
                    : payment.getPaymentDate().format(DATE_FORMAT));

            row.put("invoiceNumber", invoiceNumber);
            row.put("tenantName", tenantName);
            row.put("roomNumber", roomNumber);

            row.put("paymentMethod",
                    payment.getPaymentMethod() == null
                    ? ""
                    : payment.getPaymentMethod().toString());

            row.put("notes",
                    payment.getNotes() == null
                    ? ""
                    : payment.getNotes());

            row.put("amount",
                    payment.getAmount() == null
                    ? BigDecimal.ZERO
                    : payment.getAmount());

            totalRevenue = totalRevenue.add(
                    payment.getAmount() == null
                    ? BigDecimal.ZERO
                    : payment.getAmount()
            );

            rows.add(row);
        }

        // Parameter laporan
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("PERIOD_TEXT", periodText(start, end));
        parameters.put("TOTAL_REVENUE", totalRevenue);
        parameters.put("PAYMENT_COUNT", rows.size());

        // Mengakses template JRXML
        try (InputStream template = new ClassPathResource(
                "reports/laporan-penerimaan.jrxml"
        ).getInputStream()) {

            // Compile JRXML
            JasperReport jasperReport =
                    JasperCompileManager.compileReport(template);

            // Menghubungkan data dengan JasperReports
            JRMapCollectionDataSource dataSource =
                    new JRMapCollectionDataSource(rows);

            // Mengisi template laporan
            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(
                            jasperReport,
                            parameters,
                            dataSource
                    );

            // Export PDF
            if ("pdf".equalsIgnoreCase(format)) {

                return JasperExportManager
                        .exportReportToPdf(jasperPrint);
            }

            // Export Excel
            if ("excel".equalsIgnoreCase(format)
                    || "xlsx".equalsIgnoreCase(format)) {

                return exportExcel(jasperPrint);
            }

            throw new IllegalArgumentException(
                    "Format laporan tidak didukung."
            );

        } catch (JRException e) {

            throw new IOException(
                    "Gagal membuat laporan JasperReports.", e
            );
        }
    }

    private byte[] exportExcel(JasperPrint jasperPrint)
            throws JRException {

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        JRXlsxExporter exporter = new JRXlsxExporter();

        exporter.setExporterInput(
                new SimpleExporterInput(jasperPrint)
        );

        exporter.setExporterOutput(
                new SimpleOutputStreamExporterOutput(output)
        );

        SimpleXlsxReportConfiguration configuration =
                new SimpleXlsxReportConfiguration();

        configuration.setOnePagePerSheet(false);
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(false);

        exporter.setConfiguration(configuration);

        exporter.exportReport();

        return output.toByteArray();
    }

    private String periodText(
            LocalDate start,
            LocalDate end
    ) {

        if (start != null && end != null) {
            return start.format(DATE_FORMAT)
                    + " s/d "
                    + end.format(DATE_FORMAT);
        }

        if (start != null) {
            return "Mulai " + start.format(DATE_FORMAT);
        }

        if (end != null) {
            return "Sampai " + end.format(DATE_FORMAT);
        }

        return "Semua Periode";
    }
}
