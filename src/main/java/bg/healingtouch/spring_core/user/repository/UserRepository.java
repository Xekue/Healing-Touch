package bg.healingtouch.spring_core.user.repository;

import bg.healingtouch.spring_core.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {


    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    //Provides CRUD methods out-of-the-box:
    //save(), findById(), findAll(), deleteById()

}
