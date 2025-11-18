package bg.healingtouch.spring_core.Admin;

import bg.healingtouch.spring_core.booking.model.BookingStatus;
import bg.healingtouch.spring_core.booking.repository.BookingRepository;
import bg.healingtouch.spring_core.therapist.model.Therapist;
import bg.healingtouch.spring_core.therapist.repository.TherapistRepository;
import bg.healingtouch.spring_core.therapist.service.TherapistService;
import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.model.UserRoles;
import bg.healingtouch.spring_core.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final TherapistRepository therapistRepository;
    private final BookingRepository bookingRepository;
    private final TherapistService therapistService;

    @Transactional
    public void promoteToTherapist(UUID targetUserId, UUID adminId) {

        User user = getExistingUser(targetUserId);

        if (user.getRole() != UserRoles.CUSTOMER)
            throw new IllegalStateException("Only customers can be promoted to therapist.");

        user.setRole(UserRoles.THERAPIST);
        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);

        therapistRepository.findByUserId(user.getId()).ifPresentOrElse(
                t -> {
                    t.setActive(true);
                    t.setDeleted(false);   // RESTORE if previously deleted
                    therapistRepository.save(t);
                },
                () -> {
                    Therapist t = new Therapist();
                    t.setUser(user);
                    t.setFirstName(user.getFirstName());
                    t.setLastName(user.getLastName());
                    t.setActive(true);
                    t.setDeleted(false);
                    t.setTitle("Certified Massage Therapist");
                    t.setHourlyRate(BigDecimal.valueOf(40));
                    t.setExperienceYears(1);
                    t.setProfilePicture("/images/default-therapist.png");
                    therapistRepository.save(t);
                }
        );
    }

    @Transactional
    public void promoteToAdmin(UUID targetUserId, UUID adminId) {
        User user = getExistingUser(targetUserId);

        if (user.getRole() == UserRoles.ADMIN)
            return;

        user.setRole(UserRoles.ADMIN);
        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
    }


    @Transactional
    public void demoteUser(UUID targetUserId, UUID adminId) {
        User user = getExistingUser(targetUserId);

        if (targetUserId.equals(adminId))
            throw new IllegalStateException("An admin cannot demote themselves.");

        if (user.getRole() == UserRoles.ADMIN) {

            long adminCount = userRepository.countByRole(UserRoles.ADMIN);
            if (adminCount <= 1)
                throw new IllegalStateException("You cannot demote the last admin.");

            user.setRole(UserRoles.THERAPIST);
        }

        else if (user.getRole() == UserRoles.THERAPIST) {

            Therapist therapist = therapistRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalStateException("Therapist entry not found!"));

            boolean hasActiveBookings = bookingRepository
                    .existsByTherapistIdAndBookingStatusIn(
                            therapist.getId(),
                            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)
                    );

            if (hasActiveBookings)
                throw new IllegalStateException("Cannot demote a therapist with active bookings.");

            therapist.setActive(false);
            therapist.setDeleted(true);
            therapistRepository.save(therapist);

            user.setRole(UserRoles.CUSTOMER);
        }

        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void toggleUserStatus(UUID userId) {
        User user = getExistingUser(userId);
        user.setActive(!user.isActive());
        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void toggleTherapistStatus(UUID therapistId) {
        Therapist therapist = therapistRepository.findById(therapistId)
                .orElseThrow(() -> new EntityNotFoundException("Therapist not found"));

        if (therapist.isDeleted())
            throw new IllegalStateException("Cannot toggle status of deleted therapist.");

        therapist.setActive(!therapist.isActive());
        therapistRepository.save(therapist);
    }

    @Transactional
    public void softDeleteTherapist(UUID therapistId) {
        therapistService.softDeleteTherapist(therapistId);
    }

    private User getExistingUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
