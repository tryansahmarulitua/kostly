package com.kostly.controller;
import com.kostly.entity.RoomStatus;
import com.kostly.repository.RoomRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class DashboardController {
    private final RoomRepository rooms;
    public DashboardController(RoomRepository rooms) { this.rooms = rooms; }
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalRooms", rooms.count());
        model.addAttribute("availableRooms", rooms.countByStatus(RoomStatus.AVAILABLE));
        model.addAttribute("occupiedRooms", rooms.countByStatus(RoomStatus.OCCUPIED));
        model.addAttribute("maintenanceRooms", rooms.countByStatus(RoomStatus.MAINTENANCE));
        return "dashboard/index";
    }
}
