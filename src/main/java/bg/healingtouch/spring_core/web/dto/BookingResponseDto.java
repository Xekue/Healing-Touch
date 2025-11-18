package bg.healingtouch.spring_core.web.dto;

import bg.healingtouch.spring_core.booking.model.BookingStatus;
import bg.healingtouch.spring_core.booking.model.MassageType;
import bg.healingtouch.spring_core.booking.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {

    private UUID id;
    private UUID customerId;
    private UUID therapistId;
    private String therapistName;
    private MassageType massageType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal price;
}

//Server -> Client response