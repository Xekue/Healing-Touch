package bg.healingtouch.spring_core.review.model;

import bg.healingtouch.spring_core.booking.model.Booking;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    private int rating; //1-5 stars
    private String comment; //feedback
    private LocalDateTime createdOn;

}
