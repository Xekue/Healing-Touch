package bg.healingtouch.spring_core.web;

import bg.healingtouch.spring_core.security.AuthenticationMetadata;
import bg.healingtouch.spring_core.therapist.service.TherapistService;
import bg.healingtouch.spring_core.web.dto.TherapistResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/therapist/profile")
@RequiredArgsConstructor
public class TherapistProfileController {

    private final TherapistService therapistService;

    @GetMapping("/{id}/edit")
    public String editProfile(@PathVariable UUID id,
                              @AuthenticationPrincipal AuthenticationMetadata principal,
                              Model model) {

        UUID currentUserId = principal.getUserId();

        var therapist = therapistService.getTherapistById(id);

        if (!therapist.getUserId().equals(currentUserId)
                && !therapistService.isAdmin(currentUserId)) {
            model.addAttribute("message", "You don’t have permission to edit this profile.");
            model.addAttribute("status", 403);
            return "error";
        }

        model.addAttribute("therapist", therapist);
        return "therapists/therapist-profile";
    }

    @PostMapping("/{id}/edit")
    public String updateProfile(@PathVariable UUID id,
                                @ModelAttribute TherapistResponseDto dto,
                                @AuthenticationPrincipal bg.healingtouch.spring_core.security.AuthenticationMetadata principal) {

        UUID currentUserId = principal.getUserId();

        var therapist = therapistService.getTherapistById(id);

        if (!therapist.getUserId().equals(currentUserId)
                && !therapistService.isAdmin(currentUserId)) {
            return "error";
        }

        therapistService.updateTherapistProfile(id, dto);
        return "redirect:/therapists/" + id;
    }
}
