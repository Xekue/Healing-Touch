package bg.healingtouch.spring_core.web;

import bg.healingtouch.spring_core.client.dto.CreateReviewDto;
import bg.healingtouch.spring_core.client.dto.ReviewResponseDto;
import bg.healingtouch.spring_core.client.service.ReviewIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class FrontendController {

    @GetMapping("/")
    public String index() {
        return "index"; // returns templates/index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // returns templates/login.html
    }

    // Optional API calls through Feign
    @RestController
    @RequestMapping("/reviews")
    @RequiredArgsConstructor
    public static class ReviewGatewayController {
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
}
