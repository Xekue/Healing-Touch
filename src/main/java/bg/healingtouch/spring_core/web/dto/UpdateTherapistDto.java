package bg.healingtouch.spring_core.web.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class UpdateTherapistDto {

    @Size(max = 100, message = "Skill name too long")
    private List<String> skills;

    @Positive(message = "Experience years must be positive")
    private Integer experienceYears;

    @Positive(message = "Hourly rate must be positive")
    private BigDecimal hourlyRate;

    private String profilePicture;

    private Boolean isActive;
}
