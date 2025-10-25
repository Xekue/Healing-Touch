package bg.healingtouch.spring_core.user.model;

import bg.healingtouch.spring_core.booking.model.Booking;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String username;

    private String firstname;

    private String lastname;

    private String profilePicture;

    @Column(unique = true)
    private String email;

    @Column(unique = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRoles role;

    private boolean active;

    //Audit fields
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdOn;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

}
