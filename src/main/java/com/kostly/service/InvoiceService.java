package com.kostly.service;

import com.kostly.dto.InvoiceForm;
import com.kostly.dto.PaymentForm;
import com.kostly.entity.*;
import com.kostly.repository.InvoiceRepository;
import com.kostly.repository.PaymentRepository;
import com.kostly.repository.RentalContractRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final RentalContractRepository contractRepository;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            RentalContractRepository contractRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.contractRepository = contractRepository;
    }

    @Transactional(readOnly = true)
    public List<Invoice> findAll() {
        return invoiceRepository.findAllByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public Invoice findById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tagihan tidak ditemukan."));
    }

    public Invoice create(InvoiceForm form) {
        if (form.getContractId() == null) {
            throw new IllegalArgumentException("Pilih kontrak terlebih dahulu.");
        }

        RentalContract contract = contractRepository.findById(form.getContractId())
                .orElseThrow(() -> new IllegalArgumentException("Kontrak tidak ditemukan."));

        if (form.getDueDate().isBefore(form.getIssueDate())) {
            throw new IllegalArgumentException("Tanggal jatuh tempo tidak boleh sebelum tanggal tagihan.");
        }

        Invoice invoice = new Invoice();
        invoice.setContract(contract);
        invoice.setAmount(form.getAmount());
        invoice.setIssueDate(form.getIssueDate());
        invoice.setDueDate(form.getDueDate());
        invoice.setDescription(form.getDescription());
        invoice.setStatus(InvoiceStatus.UNPAID);

        // ponytail: sequential invoice counter reset per month skipped, add when high multi-tenant volume
        String invoiceNumber = String.format("INV-%s-%04d",
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")),
                invoiceRepository.count() + 1);
        invoice.setInvoiceNumber(invoiceNumber);

        return invoiceRepository.save(invoice);
    }

    public Payment pay(Long invoiceId, PaymentForm form) {
        Invoice invoice = findById(invoiceId);

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalArgumentException("Tagihan ini sudah lunas.");
        }

        // ponytail: partial installment payments skipped, add when partial payments requested
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setPaymentDate(form.getPaymentDate());
        payment.setAmount(form.getAmount());
        payment.setPaymentMethod(form.getPaymentMethod());
        payment.setNotes(form.getNotes());

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPayment(payment);

        Payment savedPayment = paymentRepository.save(payment);
        invoiceRepository.save(invoice);

        return savedPayment;
    }

    public void delete(Long id) {
        Invoice invoice = findById(id);
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalArgumentException("Tagihan yang sudah lunas tidak dapat dihapus.");
        }
        invoiceRepository.delete(invoice);
    }

    @Transactional(readOnly = true)
    public long countTotal() {
        return invoiceRepository.count();
    }

    @Transactional(readOnly = true)
    public long countUnpaid() {
        return invoiceRepository.countByStatus(InvoiceStatus.UNPAID);
    }

    @Transactional(readOnly = true)
    public long countPaid() {
        return invoiceRepository.countByStatus(InvoiceStatus.PAID);
    }
}
