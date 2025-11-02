package bg.healingtouch.spring_core.Admin;

import bg.healingtouch.spring_core.booking.model.BookingStatus;
import bg.healingtouch.spring_core.booking.repository.BookingRepository;
import bg.healingtouch.spring_core.booking.service.BookingService;
import bg.healingtouch.spring_core.therapist.repository.TherapistRepository;
import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.model.UserRoles;
import bg.healingtouch.spring_core.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final TherapistRepository therapistRepository;
    private final BookingRepository bookingRepository;

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);


    @Transactional
    public void demoteToCustomer(UUID targetUserId, UUID performedByAdminId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (targetUserId.equals(performedByAdminId)) {
            throw new IllegalStateException("Admin cannot demote themself.");
        }

        long adminCount = userRepository.countByRole(UserRoles.ADMIN);
        if (user.getRole() == UserRoles.ADMIN && adminCount <= 1) {
            throw new IllegalStateException("Cannot demote the last admin.");
        }

        if (user.getRole() == UserRoles.THERAPIST) {
            therapistRepository.findByUserId(user.getId())
                    .ifPresent(t -> {
                        boolean hasActiveBookings = bookingRepository
                                .existsByTherapistIdAndBookingStatusIn(
                                        t.getId(),
                                        List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)
                                );

                        if (hasActiveBookings) {
                            throw new IllegalStateException(
                                    "Cannot demote therapist with active bookings. Reassign or cancel them first."
                            );
                        }

                        t.setActive(false);
                        therapistRepository.save(t);
                    });
        }

        user.setRole(UserRoles.CUSTOMER);
        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
    }
}
