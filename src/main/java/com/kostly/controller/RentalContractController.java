package com.kostly.controller;

import com.kostly.dto.ContractForm;
import com.kostly.entity.RentalContract;
import com.kostly.entity.RoomStatus;
import com.kostly.entity.Tenant;
import com.kostly.entity.Room;
import com.kostly.repository.RoomRepository;
import com.kostly.repository.TenantRepository;
import com.kostly.service.RentalContractService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contracts")
public class RentalContractController {

    private final RentalContractService contractService;
    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;

    public RentalContractController(
            RentalContractService contractService,
            TenantRepository tenantRepository,
            RoomRepository roomRepository) {
        this.contractService = contractService;
        this.tenantRepository = tenantRepository;
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("contracts", contractService.findAll());
        return "contracts/index";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("contractForm", new ContractForm());
        model.addAttribute("tenants", tenantRepository.findAll());
        model.addAttribute(
                "rooms",
                roomRepository.findByStatus(RoomStatus.AVAILABLE)
        );

        return "contracts/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("contractForm") ContractForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("tenants", tenantRepository.findAll());
            model.addAttribute(
                    "rooms",
                    roomRepository.findByStatus(RoomStatus.AVAILABLE)
            );
            return "contracts/form";
        }

        try {
            RentalContract contract = new RentalContract();

            Tenant tenant = tenantRepository.findById(form.getTenantId())
                    .orElseThrow(()
                            -> new IllegalArgumentException("Penyewa tidak ditemukan"));

            Room room = roomRepository.findById(form.getRoomId())
                    .orElseThrow(()
                            -> new IllegalArgumentException("Kamar tidak ditemukan"));

            contract.setTenant(tenant);
            contract.setRoom(room);
            contract.setStartDate(form.getStartDate());
            contract.setEndDate(form.getEndDate());

            // Harga diambil dari data kamar di database
            contract.setMonthlyPrice(room.getPrice());

            contractService.create(contract);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Kontrak sewa berhasil disimpan."
            );

            return "redirect:/contracts";

        } catch (IllegalArgumentException e) {
            model.addAttribute("tenants", tenantRepository.findAll());
            model.addAttribute(
                    "rooms",
                    roomRepository.findByStatus(RoomStatus.AVAILABLE)
            );
            model.addAttribute("error", e.getMessage());

            return "contracts/form";
        }
    }

    private void loadFormOptions(Model model) {
        model.addAttribute("tenants", tenantRepository.findAll());
        model.addAttribute(
                "rooms",
                roomRepository.findAll().stream()
                        .filter(room
                                -> room.getStatus() == RoomStatus.AVAILABLE)
                        .toList());
    }
}
