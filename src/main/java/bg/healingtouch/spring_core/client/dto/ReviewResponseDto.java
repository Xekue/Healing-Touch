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
    private UUID therapistId;
    private UUID userId;
    private String comment;
    private int rating;
    private LocalDateTime createdOn;
}
