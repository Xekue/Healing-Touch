package bg.healingtouch.spring_core.client.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
public class ReviewResponseDto {

    private UUID id;
    private UUID bookingId;
    private UUID therapistId;
    private UUID customerId;
    private String comment;
    private int rating;
    private LocalDateTime createdOn;
    private String reviewerName;

    private LocalDateTime reviewDate;

}
