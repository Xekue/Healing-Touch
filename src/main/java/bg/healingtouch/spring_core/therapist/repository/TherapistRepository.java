package bg.healingtouch.spring_core.therapist.repository;

import bg.healingtouch.spring_core.therapist.model.Therapist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TherapistRepository extends JpaRepository<Therapist, UUID> {

    Optional<Therapist> findByUserId(UUID userId);

    Optional<Therapist> findByUserIdAndDeletedFalse(UUID userId);

    List<Therapist> findAllByActiveTrueAndDeletedFalse();

}
