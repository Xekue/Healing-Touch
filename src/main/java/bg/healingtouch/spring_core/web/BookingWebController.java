package bg.healingtouch.spring_core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingWebController {

    @GetMapping("/create")
    public String showBookingForm(Model model) {
        return "bookings/create";
    }

    @GetMapping("/my-bookings")
    public String showMyBookings() {
        return "bookings/my-bookings";
    }

    @GetMapping("/therapist-bookings")
    public String showTherapistBookings() {
        return "bookings/therapist-bookings";
    }
}
