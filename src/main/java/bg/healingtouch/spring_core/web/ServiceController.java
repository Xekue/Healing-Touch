package bg.healingtouch.spring_core.web;

import bg.healingtouch.spring_core.booking.model.MassageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ServiceController {

    @GetMapping("/services")
    public String showAllServices(Model model) {
        model.addAttribute("services", MassageType.values());
        return "services";
    }

    @GetMapping("/services/{type}")
    public String showServiceDetails(@PathVariable("type") MassageType type, Model model) {
        model.addAttribute("service", type); // ✅ match your HTML
        return "service-details";
    }
}
