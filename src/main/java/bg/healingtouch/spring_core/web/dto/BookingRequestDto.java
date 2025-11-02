package bg.healingtouch.spring_core.web.dto;

import bg.healingtouch.spring_core.booking.model.MassageType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class BookingRequestDto {

    private UUID therapistId;
    private MassageType massageType;
    private LocalDateTime startTime;
}
