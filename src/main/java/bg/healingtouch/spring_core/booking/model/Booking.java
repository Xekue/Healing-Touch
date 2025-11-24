package bg.healingtouch.spring_core.booking.model;

import bg.healingtouch.spring_core.therapist.model.Therapist;
import bg.healingtouch.spring_core.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "therapist_id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
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

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdOn;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @Column(nullable = false)
    private Integer durationMinutes;

    private String notes;

    @Column(nullable = false)
    private BigDecimal price;

    private String cancellationReason;

    @Column
    private LocalDateTime completedOn;

    @Transient
    private bg.healingtouch.spring_core.client.dto.ReviewResponseDto review;

}
