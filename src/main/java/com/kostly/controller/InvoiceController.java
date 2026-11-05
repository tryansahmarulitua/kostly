package com.kostly.controller;

import com.kostly.dto.InvoiceForm;
import com.kostly.dto.PaymentForm;
import com.kostly.entity.ContractStatus;
import com.kostly.entity.Invoice;
import com.kostly.entity.InvoiceStatus;
import com.kostly.repository.RentalContractRepository;
import com.kostly.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final RentalContractRepository contractRepository;

    public InvoiceController(InvoiceService invoiceService, RentalContractRepository contractRepository) {
        this.invoiceService = invoiceService;
        this.contractRepository = contractRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("invoices", invoiceService.findAll());
        model.addAttribute("totalCount", invoiceService.countTotal());
        model.addAttribute("unpaidCount", invoiceService.countUnpaid());
        model.addAttribute("paidCount", invoiceService.countPaid());
        return "invoices/index";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("invoiceForm", new InvoiceForm());
        model.addAttribute("contracts", contractRepository.findByStatus(ContractStatus.ACTIVE));
        return "invoices/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("invoiceForm") InvoiceForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        if (result.hasErrors()) {
            model.addAttribute("contracts", contractRepository.findByStatus(ContractStatus.ACTIVE));
            return "invoices/form";
        }

        try {
            invoiceService.create(form);
            redirect.addFlashAttribute("success", "Tagihan berhasil dibuat.");
            return "redirect:/invoices";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("contracts", contractRepository.findByStatus(ContractStatus.ACTIVE));
            model.addAttribute("error", ex.getMessage());
            return "invoices/form";
        }
    }

    @GetMapping("/{id}/pay")
    public String payForm(@PathVariable Long id, Model model) {
        Invoice invoice = invoiceService.findById(id);
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            return "redirect:/invoices/" + id;
        }

        PaymentForm form = new PaymentForm();
        form.setAmount(invoice.getAmount());

        model.addAttribute("invoice", invoice);
        model.addAttribute("paymentForm", form);
        return "invoices/pay";
    }

    @PostMapping("/{id}/pay")
    public String pay(
            @PathVariable Long id,
            @Valid @ModelAttribute("paymentForm") PaymentForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        Invoice invoice = invoiceService.findById(id);

        if (result.hasErrors()) {
            model.addAttribute("invoice", invoice);
            return "invoices/pay";
        }

        try {
            invoiceService.pay(id, form);
            redirect.addFlashAttribute("success", "Pembayaran berhasil dicatat.");
            return "redirect:/invoices/" + id;
        } catch (IllegalArgumentException ex) {
            model.addAttribute("invoice", invoice);
            model.addAttribute("error", ex.getMessage());
            return "invoices/pay";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", invoiceService.findById(id));
        return "invoices/detail";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            invoiceService.delete(id);
            redirect.addFlashAttribute("success", "Tagihan berhasil dihapus.");
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/invoices";
    }
}
