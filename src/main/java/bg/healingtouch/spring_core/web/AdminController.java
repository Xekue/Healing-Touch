package bg.healingtouch.spring_core.web;

import bg.healingtouch.spring_core.Admin.AdminService;
import bg.healingtouch.spring_core.booking.model.BookingStatus;
import bg.healingtouch.spring_core.booking.service.BookingService;
import bg.healingtouch.spring_core.user.repository.UserRepository;
import bg.healingtouch.spring_core.therapist.service.TherapistService;
import bg.healingtouch.spring_core.security.AuthenticationMetadata;
import bg.healingtouch.spring_core.user.service.UserService;
import bg.healingtouch.spring_core.web.dto.BookingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final TherapistService therapistService;
    private final AdminService adminService;
    private final BookingService bookingService;
    private final UserService userService;

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

    @GetMapping("/bookings")
    public String viewAllBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) UUID therapistId,
            @RequestParam(required = false) UUID customerId,
            Model model) {

        List<BookingResponseDto> bookings =
                bookingService.getAllBookingsFiltered(status, therapistId, customerId);

        model.addAttribute("bookings", bookings);
        model.addAttribute("statusOptions", BookingStatus.values());
        model.addAttribute("therapists", therapistService.getAllTherapists());
        model.addAttribute("customers", userService.getAllCustomers());

        return "admin/bookings";
    }
}
