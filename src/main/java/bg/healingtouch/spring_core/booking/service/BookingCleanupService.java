package bg.healingtouch.spring_core.booking.service;

import bg.healingtouch.spring_core.booking.model.BookingStatus;
import bg.healingtouch.spring_core.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@EnableScheduling
@Service
@RequiredArgsConstructor
public class BookingCleanupService {

    private final BookingRepository bookingRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void cancelStaleBookings() {
        LocalDateTime cutoff =  LocalDateTime.now().minusHours(24);
        bookingRepository.findAll().stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.PENDING && b.getStartTime().isBefore(cutoff))
                .forEach(b -> b.setBookingStatus(BookingStatus.CANCELLED));
    }

    @Scheduled(fixedRate = 60000)
    public void logBookingStatus() {
        long pendingCount = bookingRepository.count();
        log.info("Currently {} bookings in the system", pendingCount);
    }
}

//Scheduler to CLEAR PANDING bookings after 24h if not ACCEPTED
// Second Scheduler - RUNS every 60 seconds
