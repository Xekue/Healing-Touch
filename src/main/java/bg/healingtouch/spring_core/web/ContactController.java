package bg.healingtouch.spring_core.web;

import bg.healingtouch.spring_core.notification.service.EmailService;
import bg.healingtouch.spring_core.web.dto.ContactFormDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ContactController {

    private final EmailService emailService;

    @GetMapping("/contact")
    public String showContactPage(Model model) {

        if (!model.containsAttribute("contactForm")) {
            model.addAttribute("contactForm", new ContactFormDto());
        }

        if (!model.containsAttribute("success")) {
            model.addAttribute("success", null);
        }
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", null);
        }

        return "contact";
    }

    @PostMapping("/contact")
    public String handleContactSubmit(
            @Valid @ModelAttribute("contactForm") ContactFormDto form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("contactForm", form);
            redirectAttributes.addFlashAttribute("success", "Your message has been sent successfully!");
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.contactForm", bindingResult);
            return "redirect:/contact";
        }

        String body = """
                NEW CONTACT MESSAGE
                
                Name: %s
                Email: %s
                Phone: %s
                Subject: %s
                
                Message:
                %s
                """.formatted(
                form.getName(),
                form.getEmail(),
                form.getPhone() != null ? form.getPhone() : "N/A",
                form.getSubject(),
                form.getMessage()
        );

        emailService.send(
                "healingtouch101@gmail.com",
                "New Contact Message: " + form.getSubject(),
                body
        );

        emailService.send(
                form.getEmail(),
                "We received your message",
                "Hi " + form.getName() + ",\n\nThank you for contacting Healing Touch. We will respond within 24 hours.\n\nWarm regards,\nHealing Touch Team"
        );

        redirectAttributes.addFlashAttribute("success", "Your message has been sent successfully!");
        return "redirect:/contact";
    }
}
