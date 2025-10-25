package bg.healingtouch.spring_core.web.dto;

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
    private String firstName;
    private String lastName;
    private boolean active;

    private List<String> skills;
    private Integer experienceYears;
    private BigDecimal hourlyRate;
    private String profilePicture;

}
