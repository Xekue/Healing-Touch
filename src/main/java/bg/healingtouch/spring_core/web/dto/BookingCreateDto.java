package bg.healingtouch.spring_core.web.dto;

import bg.healingtouch.spring_core.booking.model.MassageType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingCreateDto {

    @NotNull(message = "Therapist is required")
    private UUID therapistId;

    @NotNull(message = "Massage type is required")
    private MassageType massageType;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @Size(max = 255, message = "Location must be less than 255 characters")
    private String location;

    private String notes;

    private Integer durationMinutes;
}

//Client -> Server response
