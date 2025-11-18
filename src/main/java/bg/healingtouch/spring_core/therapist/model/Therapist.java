package bg.healingtouch.spring_core.therapist.model;

import bg.healingtouch.spring_core.booking.model.Booking;
import bg.healingtouch.spring_core.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "therapists")
@Getter
@Setter
public class Therapist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;

    private String lastName;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "therapist_skills", joinColumns = @JoinColumn(name = "therapist_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @Column(nullable = false)
    private int experienceYears;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdOn;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "therapist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    private String profilePicture;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "therapist_specialties", joinColumns = @JoinColumn(name = "therapist_id"))
    @Column(name = "specialty")
    private List<String> specialties = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "therapist_certifications", joinColumns = @JoinColumn(name = "therapist_id"))
    @Column(name = "certification")
    private List<String> certifications = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "therapist_languages", joinColumns = @JoinColumn(name = "therapist_id"))
    @Column(name = "language")
    private List<String> languages = new ArrayList<>();

    private Integer totalSessions = 0;

    private Double rating = 0.0;

    private String title;

    private String location;

    private String availability;

}
