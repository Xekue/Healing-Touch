package bg.healingtouch.spring_core.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class AddTherapistDto {

    @NotNull(message = "User ID is required")
    private UUID userId;

    private List<String> skills;

    @Positive(message = "Experience must be positive")
    private int experienceYears;

    @Positive(message = "Hourly rate must be positive")
    private BigDecimal hourlyRate;

    private boolean active;

    private String profilePicture;
}
