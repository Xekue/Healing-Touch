package bg.healingtouch.spring_core.client;

import bg.healingtouch.spring_core.client.dto.CreateReviewDto;
import bg.healingtouch.spring_core.client.dto.ReviewResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "review-service", url = "http://localhost:8081", path = "/api/reviews")
public interface ReviewClient {

    @PostMapping
    ReviewResponseDto createReview(@RequestBody CreateReviewDto dto);

    @GetMapping("/therapist/{therapistId}")
    List<ReviewResponseDto> getReviewsForTherapist(@PathVariable UUID therapistId);

    @DeleteMapping("/{id}")
    void deleteReview(@PathVariable UUID id);
}
