package bg.healingtouch.spring_core.review.repository;

import bg.healingtouch.spring_core.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
}
