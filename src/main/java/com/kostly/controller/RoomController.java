package com.kostly.controller;
import com.kostly.entity.Room;
import com.kostly.repository.RoomRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/rooms")
public class RoomController {
    private final RoomRepository rooms;
    public RoomController(RoomRepository rooms) { this.rooms = rooms; }
    @GetMapping public String index(Model model) {
        model.addAttribute("rooms", rooms.findAll());
        return "rooms/index";
    }
    @GetMapping("/new") public String createForm(Model model) {
        model.addAttribute("room", new Room());
        return "rooms/form";
    }
    @PostMapping public String save(@Valid @ModelAttribute("room") Room room, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) return "rooms/form";
        try {
            rooms.save(room);
            redirect.addFlashAttribute("success", "Data kamar berhasil disimpan.");
            return "redirect:/rooms";
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            model.addAttribute("error", "Nomor kamar sudah digunakan.");
            return "rooms/form";
        }
    }
    @GetMapping("/{id}/edit") public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("room", rooms.findById(id).orElseThrow());
        return "rooms/form";
    }
    @PostMapping("/{id}/edit") public String update(@PathVariable Long id, @Valid @ModelAttribute("room") Room room,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) return "rooms/form";
        room.setId(id);
        try {
            rooms.save(room);
            redirect.addFlashAttribute("success", "Data kamar berhasil diperbarui.");
            return "redirect:/rooms";
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            model.addAttribute("error", "Nomor kamar sudah digunakan.");
            return "rooms/form";
        }
    }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        rooms.deleteById(id);
        redirect.addFlashAttribute("success", "Data kamar berhasil dihapus.");
        return "redirect:/rooms";
    }
}
