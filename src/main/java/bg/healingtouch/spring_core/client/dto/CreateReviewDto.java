package bg.healingtouch.spring_core.client.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateReviewDto {

    private UUID bookingId;
    private UUID therapistId;
    private UUID customerId;
    private int rating;
    private String comment;
}
