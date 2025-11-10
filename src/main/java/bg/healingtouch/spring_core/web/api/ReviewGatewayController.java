package bg.healingtouch.spring_core.web.api;

import bg.healingtouch.spring_core.client.dto.CreateReviewDto;
import bg.healingtouch.spring_core.client.dto.ReviewResponseDto;
import bg.healingtouch.spring_core.client.service.ReviewIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewGatewayController {

    private final ReviewIntegrationService reviewIntegrationService;

    @PostMapping
    public ReviewResponseDto createReview(@RequestBody CreateReviewDto dto) {
        return reviewIntegrationService.createReview(dto);
    }

    @GetMapping("/therapist/{id}")
    public List<ReviewResponseDto> getReviewsForTherapist(@PathVariable UUID id) {
        return reviewIntegrationService.getReviewsForTherapist(id);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable UUID id) {
        reviewIntegrationService.deleteReview(id);
    }


}
