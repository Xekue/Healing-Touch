package bg.healingtouch.spring_core.client.service;

import bg.healingtouch.spring_core.client.ReviewClient;
import bg.healingtouch.spring_core.client.dto.CreateReviewDto;
import bg.healingtouch.spring_core.client.dto.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewIntegrationService {

    private final ReviewClient reviewClient;

    public ReviewResponseDto createReview(CreateReviewDto dto) {
        return reviewClient.createReview(dto);
    }

    public List<ReviewResponseDto> getReviewsForTherapist(UUID therapistId) {
        return reviewClient.getReviewsForTherapist(therapistId);
    }

    public void deleteReview(UUID reviewId) {
        reviewClient.deleteReview(reviewId);
    }
}
