package com.kostly.controller;

import com.kostly.entity.Tenant;
import com.kostly.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("tenants", tenantService.findAll());
        return "tenants/index";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("tenant", new Tenant());
        return "tenants/form";
    }

    @PostMapping
    public String save(
            @Valid @ModelAttribute("tenant") Tenant tenant,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        if (result.hasErrors()) {
            return "tenants/form";
        }

        try {
            tenantService.save(tenant);
            redirect.addFlashAttribute(
                    "success", "Data penghuni berhasil disimpan.");
            return "redirect:/tenants";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "tenants/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id, Model model) {

        model.addAttribute("tenant", tenantService.findById(id));
        return "tenants/form";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("tenant") Tenant tenant,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        tenant.setId(id);

        if (result.hasErrors()) {
            return "tenants/form";
        }

        try {
            tenantService.save(tenant);
            redirect.addFlashAttribute(
                    "success", "Data penghuni berhasil diperbarui.");
            return "redirect:/tenants";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "tenants/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirect) {
        try {
            tenantService.delete(id);
            redirect.addFlashAttribute(
                    "success", "Data penghuni berhasil dihapus.");
        } catch (Exception ex) {
            redirect.addFlashAttribute(
                    "error",
                    "Penghuni tidak dapat dihapus. Periksa kontrak terkait.");
        }

        return "redirect:/tenants";
    }
}