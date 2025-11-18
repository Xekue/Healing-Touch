package bg.healingtouch.spring_core.web;

import bg.healingtouch.spring_core.Admin.AdminService;
import bg.healingtouch.spring_core.user.repository.UserRepository;
import bg.healingtouch.spring_core.therapist.service.TherapistService;
import bg.healingtouch.spring_core.security.AuthenticationMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final TherapistService therapistService;
    private final AdminService adminService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("therapists", therapistService.getAllTherapists());
        model.addAttribute("loggedUserId", getLoggedAdminId());
        return "admin/dashboard";
    }

    private UUID getLoggedAdminId() {
        AuthenticationMetadata metadata =
                (AuthenticationMetadata) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal();
        return metadata.getUserId();
    }

    @PostMapping("/users/{id}/promote-therapist")
    public String promoteToTherapist(@PathVariable UUID id) {
        adminService.promoteToTherapist(id, getLoggedAdminId());
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/promote-admin")
    public String promoteToAdmin(@PathVariable UUID id) {
        adminService.promoteToAdmin(id, getLoggedAdminId());
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/demote")
    public String demote(@PathVariable UUID id) {
        adminService.demoteUser(id, getLoggedAdminId());
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable UUID id) {
        adminService.toggleUserStatus(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/therapists/{id}/toggle-status")
    public String toggleTherapistStatus(@PathVariable UUID id) {
        adminService.toggleTherapistStatus(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/therapists/{id}/delete")
    public String softDeleteTherapist(@PathVariable UUID id) {
        adminService.softDeleteTherapist(id);
        return "redirect:/admin/dashboard";
    }
}
