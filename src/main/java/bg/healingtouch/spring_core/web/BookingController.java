package bg.healingtouch.spring_core.web;

import bg.healingtouch.spring_core.booking.model.MassageType;
import bg.healingtouch.spring_core.booking.service.BookingService;
import bg.healingtouch.spring_core.security.AuthenticationMetadata;
import bg.healingtouch.spring_core.therapist.service.TherapistService;
import bg.healingtouch.spring_core.web.dto.BookingCreateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final TherapistService therapistService;

    @GetMapping("/new")
    public String showCreateForm(Model model) {

        if (!model.containsAttribute("booking")) {
            model.addAttribute("booking", new BookingCreateDto());
            model.addAttribute("org.springframework.validation.BindingResult.booking",
                    new BeanPropertyBindingResult(new BookingCreateDto(), "booking"));
        }

        model.addAttribute("therapists", therapistService.findAllActiveTherapists());
        model.addAttribute("massageTypes", MassageType.values());

        return "bookings/create";
    }

    @PostMapping("/new")
    public String createBooking(@Valid @ModelAttribute("booking") BookingCreateDto bookingDto,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("booking", bookingDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.booking", bindingResult);
            return "redirect:/bookings/new";
        }

        try {
            AuthenticationMetadata auth = (AuthenticationMetadata) userDetails;
            UUID userId = auth.getUserId();

            bookingService.createBooking(bookingDto, userId);
            redirectAttributes.addFlashAttribute("success", "Your booking request has been submitted successfully!");
            return "redirect:/bookings/my";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while creating your booking. Please try again.");
            return "redirect:/bookings/new";
        }
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public String viewMyBookings(
            @AuthenticationPrincipal AuthenticationMetadata auth,
            Model model,
            @ModelAttribute("success") String success,
            @ModelAttribute("error") String error
    ) {

        UUID userId = auth.getUserId();

        var therapistOpt = therapistService.findByUserId(userId);

        if (therapistOpt.isPresent()) {

            return "redirect:/bookings/therapist";
        }

        model.addAttribute("myBookings", bookingService.getBookingsForUser(userId));

        if (success != null && !success.isBlank()) {
            model.addAttribute("success", success);
        }
        if (error != null && !error.isBlank()) {
            model.addAttribute("error", error);
        }

        return "bookings/my-bookings";
    }

    @PreAuthorize("hasRole('THERAPIST')")
    @GetMapping("/therapist")
    public String viewTherapistBookings(
            @AuthenticationPrincipal AuthenticationMetadata auth,
            Model model) {

        UUID userId = auth.getUserId();

        UUID therapistId = therapistService
                .findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Therapist profile not found"))
                .getId();

        model.addAttribute("therapistBookings",
                bookingService.getTherapistBookingsDto(therapistId));

        return "bookings/therapist-bookings";
    }

    @GetMapping
    public String redirectToNewBooking() {
        return "redirect:/bookings/new";
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable UUID id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        bookingService.cancelBooking(id, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("success", "Booking canceled successfully!");
        return "redirect:/bookings/my";
    }

    @PreAuthorize("hasRole('THERAPIST')")
    @PostMapping("/{id}/approve")
    public String approveBooking(@PathVariable UUID id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            bookingService.approveBooking(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Booking approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings/therapist";
    }

    @PostMapping("/{id}/decline")
    public String declineBooking(@PathVariable("id") UUID bookingId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            bookingService.declineBooking(bookingId, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Booking declined successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings/therapist";
    }

    @PostMapping("/{id}/complete")
    public String completeBooking(@PathVariable("id") UUID bookingId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        try {
            bookingService.completeBooking(bookingId, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Booking completed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/bookings/therapist";
    }
}
