package bg.healingtouch.spring_core.booking.service;

import bg.healingtouch.spring_core.booking.model.Booking;
import bg.healingtouch.spring_core.booking.model.BookingStatus;
import bg.healingtouch.spring_core.booking.model.PaymentStatus;
import bg.healingtouch.spring_core.booking.repository.BookingRepository;
import bg.healingtouch.spring_core.client.ReviewClient;
import bg.healingtouch.spring_core.client.dto.CreateReviewDto;
import bg.healingtouch.spring_core.client.dto.ReviewResponseDto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TherapistRepository therapistRepository;
    private final UserRepository userRepository;
    private final ReviewClient reviewClient;
    private final UserService userService;
    private final TherapistService therapistService;

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

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

        return new BookingResponseDto(
                booking.getId(),
                customer.getId(),
                therapist.getId(),
                therapist.getFirstName() + " " + therapist.getLastName(),
                booking.getMassageType(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getBookingStatus(),
                booking.getPaymentStatus(),
                booking.getPrice()
        );
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
        return new BookingResponseDto(
                booking.getId(),
                booking.getCustomer().getId(),
                booking.getTherapist().getId(),
                booking.getTherapist().getUser().getFirstName() + " " +
                        booking.getTherapist().getUser().getLastName(),
                booking.getMassageType(),
                booking.getStartTime(),
                booking.getEndTime(),
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
        bookingRepository.save(booking);
    }

    @Transactional
    public ReviewResponseDto submitReview(UUID bookingId, String username, int rating, String comment) {
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("You can only review your own bookings");
        }

        CreateReviewDto dto = new CreateReviewDto();
        dto.setBookingId(booking.getId());
        dto.setTherapistId(booking.getTherapist().getId());
        dto.setCustomerId(customer.getId());
        dto.setRating(rating);
        dto.setComment(comment);

        return reviewClient.createReview(dto);
    }


    @Transactional
    public void approveBooking(UUID bookingId, String therapistUsername) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (!booking.getTherapist().getUser().getUsername().equals(therapistUsername)) {
            throw new SecurityException("You can only approve your own bookings.");
        }

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be approved.");
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        mapToResponseDto(booking);
    }

    @Transactional
    public void declineBooking(UUID bookingId, String therapistUsername) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (!booking.getTherapist().getUser().getUsername().equals(therapistUsername)) {
            throw new SecurityException("You can only decline your own bookings.");
        }

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be declined.");
        }

        booking.setBookingStatus(BookingStatus.DECLINED);
        bookingRepository.save(booking);
        mapToResponseDto(booking);
    }

    public List<BookingResponseDto> getBookingsForTherapist(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Therapist therapist = therapistRepository.findByUserIdAndDeletedFalse(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Therapist not found or is deleted"));

        return bookingRepository.findByTherapistId(therapist.getId())
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional
    public void completeBooking(UUID bookingId, String therapistUsername) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (!booking.getTherapist().getUser().getUsername().equals(therapistUsername)) {
            throw new SecurityException("You can only complete your own bookings.");
        }

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be marked as completed.");
        }

        booking.setBookingStatus(BookingStatus.COMPLETED);
        booking.setUpdatedOn(LocalDateTime.now());
        booking.setCompletedOn(LocalDateTime.now());
        bookingRepository.save(booking);
    }
}
