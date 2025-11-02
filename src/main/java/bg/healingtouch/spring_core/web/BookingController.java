package bg.healingtouch.spring_core.web;

import bg.healingtouch.spring_core.booking.model.Booking;
import bg.healingtouch.spring_core.booking.service.BookingService;
import bg.healingtouch.spring_core.therapist.service.TherapistService;
import bg.healingtouch.spring_core.web.dto.BookingCreateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        }

        model.addAttribute("therapists", therapistService.findAllActiveTherapists());

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

        bookingService.createBooking(bookingDto, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("success", "Booking requested successfully!");
        return "redirect:/bookings/my";
    }

    // Client Bookings VIEW
    @GetMapping("/my")
    public String viewMyBookings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("myBookings", bookingService.getBookingsForUser(userDetails.getUsername()));
        return "booking/my-bookings";
    }

    //Therapist Bookings View
    @GetMapping("/therapist")
    public String viewTherapistBookings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("therapistBookings", bookingService.getBookingsForTherapist(userDetails.getUsername()));
        return "bookings/therapist-bookings";
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable("id") String bookingId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        bookingService.cancelBooking(UUID.fromString(bookingId), userDetails.getUsername());
        redirectAttributes.addFlashAttribute("success", "Booking canceled successfully!");
        return "redirect:/bookings/my";
    }

    // APPROVE and DECLINE logic
    @PostMapping("/{id}/approve")
    public String approveBooking(@PathVariable("id") UUID bookingId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            bookingService.approveBooking(bookingId, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Booking approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/therapist/bookings";
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
        return "redirect:/therapist/bookings";
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
        return "redirect:/therapist/bookings";
    }


}
