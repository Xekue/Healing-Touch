package bg.healingtouch.spring_core.client.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateReviewDto {

    @NotNull(message = "Booking ID is required.")
    private UUID bookingId;

    @NotNull(message = "Therapist ID is required.")
    private UUID therapistId;

    @NotNull(message = "Customer ID is required.")
    private UUID customerId;

    @Min(value = 1, message = "Rating must be at least 1.")
    @Max(value = 5, message = "Rating must be no more than 5.")
    private int rating;

    @Size(max = 500, message = "Comment cannot exceed 500 characters.")
    private String comment;
}
