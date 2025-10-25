package bg.healingtouch.spring_core.therapist.repository;

import bg.healingtouch.spring_core.therapist.model.Therapist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TherapistRepository extends JpaRepository<Therapist, UUID> {

    List<Therapist> findByActiveTrue();
}
