package bg.healingtouch.spring_core.booking.service;

import bg.healingtouch.spring_core.booking.model.Booking;
import bg.healingtouch.spring_core.booking.model.BookingStatus;
import bg.healingtouch.spring_core.booking.model.PaymentStatus;
import bg.healingtouch.spring_core.booking.repository.BookingRepository;
import bg.healingtouch.spring_core.notification.service.EmailService;
import bg.healingtouch.spring_core.therapist.model.Therapist;
import bg.healingtouch.spring_core.therapist.repository.TherapistRepository;
import bg.healingtouch.spring_core.therapist.service.TherapistService;
import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.repository.UserRepository;
import bg.healingtouch.spring_core.user.service.UserService;
import bg.healingtouch.spring_core.web.dto.BookingCreateDto;
import bg.healingtouch.spring_core.web.dto.BookingResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TherapistRepository therapistRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TherapistService therapistService;
    private final EmailService emailService;


    @Transactional
    public BookingResponseDto createBooking(BookingCreateDto dto, UUID customerId) {

        User customer = userService.getById(customerId);
        Therapist therapist = therapistService.getById(dto.getTherapistId());

        BigDecimal price = calculatePrice(dto.getDurationMinutes());
        LocalDateTime endTime = dto.getStartTime().plusMinutes(dto.getDurationMinutes());

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setTherapist(therapist);
        booking.setMassageType(dto.getMassageType());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(endTime);
        booking.setDurationMinutes(dto.getDurationMinutes());
        booking.setNotes(dto.getNotes());
        booking.setLocation(dto.getLocation());
        booking.setPrice(price);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.PENDING);

        bookingRepository.save(booking);

        return mapToResponseDto(booking);
    }


    private BigDecimal calculatePrice(Integer duration) {
        if (duration == null) return BigDecimal.ZERO;

        return switch (duration) {
            case 30 -> BigDecimal.valueOf(45);
            case 60 -> BigDecimal.valueOf(60);
            case 90 -> BigDecimal.valueOf(90);
            case 120 -> BigDecimal.valueOf(120);
            default -> BigDecimal.ZERO;
        };
    }


    private BookingResponseDto mapToResponseDto(Booking booking) {

        String customerName =
                booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName();

        String therapistName =
                booking.getTherapist().getUser().getFirstName() + " " +
                        booking.getTherapist().getUser().getLastName();

        return new BookingResponseDto(
                booking.getId(),
                booking.getCustomer().getId(),
                customerName,
                booking.getTherapist().getId(),
                therapistName,
                booking.getMassageType(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getDurationMinutes(),
                booking.getNotes(),
                booking.getBookingStatus(),
                booking.getPaymentStatus(),
                booking.getPrice()
        );
    }


    public List<BookingResponseDto> getBookingsForUser(UUID userId) {
        return bookingRepository.findByCustomerId(userId)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }


    private void assertTherapistOwnership(Booking booking, String therapistUsername) {
        if (!booking.getTherapist().getUser().getUsername().equals(therapistUsername)) {
            throw new SecurityException("You can only manage your own bookings.");
        }
    }


    @Transactional
    public void cancelBooking(UUID bookingId, String username) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (!booking.getCustomer().getUsername().equals(username)) {
            throw new SecurityException("You can cancel only your own bookings.");
        }

        if (booking.getBookingStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Completed bookings cannot be canceled.");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking already canceled.");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
    }


    @Transactional
    public void approveBooking(UUID bookingId, String therapistUsername) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        assertTherapistOwnership(booking, therapistUsername);

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be approved.");
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);

        emailService.send(
                booking.getCustomer().getEmail(),
                "Your booking is confirmed!",
                "Your therapist has approved your booking scheduled for: " +
                        booking.getStartTime()
        );
    }


    @Transactional
    public void declineBooking(UUID bookingId, String therapistUsername) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        assertTherapistOwnership(booking, therapistUsername);

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be declined.");
        }

        booking.setBookingStatus(BookingStatus.DECLINED);

        emailService.send(
                booking.getCustomer().getEmail(),
                "Your booking was declined",
                "Unfortunately, your therapist declined your session request."
        );
    }

    @Transactional
    public void completeBooking(UUID bookingId, String therapistUsername) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        assertTherapistOwnership(booking, therapistUsername);

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be marked as completed.");
        }

        booking.setBookingStatus(BookingStatus.COMPLETED);
        booking.setCompletedOn(LocalDateTime.now());

        emailService.send(
                booking.getCustomer().getEmail(),
                "Your session is completed",
                "Thank you for visiting HealingTouch! We hope you enjoyed your session."
        );
    }

    public List<BookingResponseDto> getAllBookingsFiltered(
            BookingStatus status,
            UUID therapistId,
            UUID customerId
    ) {
        return bookingRepository.findAll()
                .stream()
                .filter(b -> status == null || b.getBookingStatus() == status)
                .filter(b -> therapistId == null || b.getTherapist().getId().equals(therapistId))
                .filter(b -> customerId == null || b.getCustomer().getId().equals(customerId))
                .sorted(Comparator.comparing(Booking::getStartTime))
                .map(this::mapToResponseDto)
                .toList();
    }

    public List<Booking> getBookingsForTherapist(UUID therapistId) {
        return bookingRepository.findByTherapistId(therapistId);
    }

    public List<BookingResponseDto> getTherapistBookingsDto(UUID therapistId) {
        return bookingRepository.findByTherapistId(therapistId)
                .stream()
                .sorted(Comparator.comparing(Booking::getStartTime))
                .map(this::mapToResponseDto)
                .toList();
    }
}
