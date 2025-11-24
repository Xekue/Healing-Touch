package bg.healingtouch.spring_core.user.repository;

import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.model.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {


    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    long countByRole(UserRoles role);

    boolean existsByUsername(String username);

    boolean existsByRole(UserRoles role);

    List<User> findAllByRole(UserRoles role);
}
