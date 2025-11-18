package bg.healingtouch.spring_core.web;

import bg.healingtouch.spring_core.client.ReviewClient;
import bg.healingtouch.spring_core.client.dto.CreateReviewDto;
import bg.healingtouch.spring_core.client.dto.ReviewResponseDto;
import bg.healingtouch.spring_core.security.AuthenticationMetadata;
import bg.healingtouch.spring_core.therapist.service.TherapistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Controller
@RequestMapping("/therapists")
@RequiredArgsConstructor
public class TherapistWebController {

    private final TherapistService therapistService;
    private final ReviewClient reviewClient;

    @GetMapping
    public String listTherapists(Model model) {
        model.addAttribute("therapists", therapistService.findAllActiveTherapists());
        return "therapists/list";
    }

    @GetMapping("/{id}")
    public String getTherapistDetails(@PathVariable UUID id, Model model, Principal principal) {

        var therapist = therapistService.getTherapistById(id);

        List<ReviewResponseDto> reviews = List.of();
        try {
            reviews = reviewClient.getReviewsForTherapist(id);
            reviews.sort((a, b) -> b.getCreatedOn().compareTo(a.getCreatedOn()));
        } catch (Exception e) {
            log.warn("Could not load reviews: {}");
        }

        double avgRating = reviews.isEmpty()
                ? 0.0
                : reviews.stream().mapToInt(ReviewResponseDto::getRating).average().orElse(0.0);

        model.addAttribute("therapist", therapist);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", avgRating);
        model.addAttribute("averageRatingInt", (int) Math.round(avgRating));
        model.addAttribute("reviewCount", reviews.size());
        model.addAttribute("canReview", principal != null);
        model.addAttribute("loggedIn", principal != null);

        return "therapists/therapist-profile";
    }

    @PostMapping("/{id}/review")
    public String addReview(@PathVariable UUID id,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            @AuthenticationPrincipal AuthenticationMetadata authUser) {

        if (authUser == null) {

            return "redirect:/login?error=loginRequired";
        }

        CreateReviewDto dto = new CreateReviewDto();
        dto.setTherapistId(id);
        dto.setCustomerId(authUser.getUserId());
        dto.setBookingId(UUID.randomUUID());
        dto.setRating(rating);
        dto.setComment(comment);

        reviewClient.createReview(dto);

        return "redirect:/therapists/" + id;
    }

}
