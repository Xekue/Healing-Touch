package bg.healingtouch.spring_core.web;

import bg.healingtouch.spring_core.therapist.service.TherapistService;
import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.model.UserRoles;
import bg.healingtouch.spring_core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final TherapistService therapistService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("therapists", therapistService.getAllTherapists());
        return "admin/dashboard";
    }

    @PostMapping("/users/{id}/promote-therapist")
    public String promoteToTherapist(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == UserRoles.CUSTOMER) {
            user.setRole(UserRoles.THERAPIST);
            userRepository.save(user);

            // Create a therapist record if one doesn't exist
            therapistService.getTherapistByUserId(user.getId()).ifPresentOrElse(
                    t -> {},
                    () -> {
                        var dto = new bg.healingtouch.spring_core.web.dto.AddTherapistDto();
                        dto.setUserId(user.getId());
                        dto.setSkills(List.of("Massage Therapy"));
                        dto.setExperienceYears(1);
                        dto.setHourlyRate(BigDecimal.valueOf(40));
                        dto.setActive(true);
                        dto.setProfilePicture("/images/default-therapist.jpg");
                        therapistService.addTherapist(dto);
                    }
            );
        }

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/promote-admin")
    public String promoteToAdmin(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != UserRoles.ADMIN) {
            user.setRole(UserRoles.ADMIN);
            userRepository.save(user);
        }

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/demote")
    public String demoteUser(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        switch (user.getRole()) {
            case ADMIN -> user.setRole(UserRoles.THERAPIST);
            case THERAPIST -> user.setRole(UserRoles.CUSTOMER);
            default -> { /* CUSTOMER stays the same */ }
        }

        userRepository.save(user);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(!user.isActive());
        userRepository.save(user);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/therapists/{id}/toggle-status")
    public String toggleTherapistStatus(@PathVariable UUID id) {
        therapistService.toggleActiveStatus(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/therapists/{id}/delete")
    public String deleteTherapist(@PathVariable UUID id) {
        therapistService.deleteTherapist(id);
        return "redirect:/admin/dashboard";
    }
}
