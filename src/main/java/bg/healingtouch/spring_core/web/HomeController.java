package bg.healingtouch.spring_core.web;

import bg.healingtouch.spring_core.security.AuthenticationMetadata;
import bg.healingtouch.spring_core.therapist.service.TherapistService;
import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.service.UserService;
import bg.healingtouch.spring_core.web.dto.LoginRequest;
import bg.healingtouch.spring_core.web.dto.RegisterDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final TherapistService therapistService;


    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/login")
    public String getLoginPage(@RequestParam(value = "error", required = false) String errorParam,
                               @ModelAttribute(value = "successMessage") String successMessage,
                               Model model) {

        model.addAttribute("loginRequest", new LoginRequest());

        if (errorParam != null) {
            model.addAttribute("errorMessage", "Incorrect username or password!");
        }

        if (successMessage != null && !successMessage.isBlank()) {
            model.addAttribute("successMessage", successMessage);
        }

        return "login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterDto());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerNewUser(@Valid @ModelAttribute("registerRequest") RegisterDto registerDto,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerRequest", bindingResult);
            redirectAttributes.addFlashAttribute("registerRequest", registerDto);
            redirectAttributes.addFlashAttribute("errorMessage", "Please correct the highlighted errors.");
            return "redirect:/register";
        }

        userService.registerNewUser(registerDto);

        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String getProfilePage(@AuthenticationPrincipal AuthenticationMetadata auth,
                                 Model model,
                                 @ModelAttribute(value = "successMessage") String successMessage,
                                 @ModelAttribute(value = "errorMessage") String errorMessage) {

        if (auth == null) {
            return "redirect:/login";
        }

        User user = userService.getById(auth.getUserId());
        model.addAttribute("user", user);

        if (successMessage != null && !successMessage.isBlank()) {
            model.addAttribute("successMessage", successMessage);
        }

        if (errorMessage != null && !errorMessage.isBlank()) {
            model.addAttribute("errorMessage", errorMessage);
        }

        return "profile";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }

}
