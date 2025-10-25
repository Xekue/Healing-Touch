package bg.healingtouch.spring_core.booking.model;

import bg.healingtouch.spring_core.review.model.Review;
import bg.healingtouch.spring_core.therapist.model.Therapist;
import bg.healingtouch.spring_core.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    //Many bookings can belong to one customer
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // Many bookings can belong to one therapist
    @ManyToOne
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MassageType massageType;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    //Audit fields
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdOn;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Review review;
    
}
