package bg.healingtouch.spring_core.booking.repository;

import bg.healingtouch.spring_core.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {



}
