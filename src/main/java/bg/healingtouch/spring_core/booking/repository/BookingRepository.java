package bg.healingtouch.spring_core.booking.repository;

import bg.healingtouch.spring_core.booking.model.Booking;
import bg.healingtouch.spring_core.booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {


    boolean existsByTherapistIdAndBookingStatusIn(UUID id, List<BookingStatus> pending);

    List<Booking> findByCustomerId(UUID customerId);

    List<Booking> findByTherapistId(UUID therapistId);
}
