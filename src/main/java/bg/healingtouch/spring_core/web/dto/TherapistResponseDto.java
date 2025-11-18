package bg.healingtouch.spring_core.web.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TherapistResponseDto {

    private UUID id;

    private UUID userId;

    private String firstName;

    private String lastName;

    private String profilePicture;

    private String title;

    private Double rating;

    @Size(max = 100, message = "Skill name too long")
    private List<String> skills;

    private String bio;

    private List<String> specialties;

    private List<String> certifications;

    private List<String> languages;

    @Positive(message = "Experience years must be positive")
    private Integer experienceYears;

    private int totalSessions;

    private String availability;

    @Positive(message = "Hourly rate must be positive")
    private BigDecimal hourlyRate;

    private Boolean active;

    private String location;

    private Boolean deleted;

}
