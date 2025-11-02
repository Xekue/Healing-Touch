package bg.healingtouch.spring_core.booking.service;

import bg.healingtouch.spring_core.booking.model.Booking;
import bg.healingtouch.spring_core.booking.model.BookingStatus;
import bg.healingtouch.spring_core.booking.model.MassageType;
import bg.healingtouch.spring_core.booking.model.PaymentStatus;
import bg.healingtouch.spring_core.booking.repository.BookingRepository;
import bg.healingtouch.spring_core.client.ReviewClient;
import bg.healingtouch.spring_core.client.dto.CreateReviewDto;
import bg.healingtouch.spring_core.client.dto.ReviewResponseDto;
import bg.healingtouch.spring_core.therapist.model.Therapist;
import bg.healingtouch.spring_core.therapist.repository.TherapistRepository;
import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.repository.UserRepository;
import bg.healingtouch.spring_core.web.dto.BookingCreateDto;
import bg.healingtouch.spring_core.web.dto.BookingResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    @Transactional
    public BookingResponseDto createBooking(@Valid BookingCreateDto dto, String username) {
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Therapist therapist = therapistRepository.findById(dto.getTherapistId())
                .orElseThrow(() -> new EntityNotFoundException("Therapist not found"));

        MassageType massageType = dto.getMassageType();
        int duration = massageType.getDurationMinutes();
        LocalDateTime endTime = dto.getStartTime().plusMinutes(duration);

        boolean conflict = bookingRepository
                .existsByTherapistIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        therapist.getId(), endTime, dto.getStartTime());

        if (conflict) {
            throw new IllegalStateException("Therapist not available at selected time");
        }

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setTherapist(therapist);
        booking.setMassageType(massageType);
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(endTime);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.PENDING);

        bookingRepository.save(booking);

        return new BookingResponseDto(
                booking.getId(),
                customer.getId(),
                therapist.getId(),
                massageType,
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getBookingStatus(),
                booking.getPaymentStatus(),
                massageType.getBasePrice()
        );
    }

    private BookingResponseDto mapToResponseDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setCustomerId(booking.getCustomer().getId());
        dto.setTherapistId(booking.getTherapist().getId());
        dto.setMassageType(booking.getMassageType());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getBookingStatus());
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setPrice(null); // add this for pricing logic if added later!!!
        return dto;
    }

    public List<BookingResponseDto> getBookingsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return bookingRepository.findByCustomerId(user.getId())
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

    //REVIEW LOGIC
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

    //APPROVE and DECLINE logic!!!
    @Transactional
    public BookingResponseDto approveBooking(UUID bookingId, String therapistUsername) {
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
        return mapToResponseDto(booking);
    }

    @Transactional
    public BookingResponseDto declineBooking(UUID bookingId, String therapistUsername) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (!booking.getTherapist().getUser().getUsername().equals(therapistUsername)) {
            throw new SecurityException("You can only decline your own bookings.");
        }

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be declined.");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        return mapToResponseDto(booking);
    }

    public List<BookingResponseDto> getBookingsForTherapist(String username) {
        Therapist therapist = therapistRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Therapist not found"));

        return bookingRepository.findByTherapistId(therapist.getId())
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    //COMPLETE Booking
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
