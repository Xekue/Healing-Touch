package bg.healingtouch.spring_core.web;

import bg.healingtouch.spring_core.therapist.service.TherapistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/therapists")
@RequiredArgsConstructor
public class TherapistWebController {

    private final TherapistService therapistService;

    @GetMapping
    public String listTherapists(Model model) {
        model.addAttribute("therapists", therapistService.findAllActiveTherapists());
        return "therapists/list";
    }

    @GetMapping("/{id}")
    public String getTherapistDetails(@PathVariable UUID id, Model model) {
        model.addAttribute("therapist", therapistService.getTherapistById(id));
        return "therapists/details";
    }

}
