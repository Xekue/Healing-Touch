package bg.healingtouch.spring_core.user.repository;

import bg.healingtouch.spring_core.booking.model.BookingStatus;
import bg.healingtouch.spring_core.therapist.model.Therapist;
import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.model.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {


    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    //Provides CRUD methods out-of-the-box:
    //save(), findById(), findAll(), deleteById()

    // UserRepository
    long countByRole(UserRoles role);

    // TherapistRepository
    Optional<Therapist> findByUserId(UUID userId);

    // BookingRepository
    boolean existsByTherapistIdAndBookingStatusIn(UUID therapistId, List<BookingStatus> statuses);

}
