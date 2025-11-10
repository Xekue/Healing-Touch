package bg.healingtouch.spring_core.config;

import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.model.UserRoles;
import bg.healingtouch.spring_core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Check if there is already an admin
        boolean adminExists = userRepository.existsByRole(UserRoles.ADMIN);

        if (!adminExists) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("admin@healingtouch.bg");
            admin.setPassword(passwordEncoder.encode("admin123")); //This password can be changed
            admin.setRole(UserRoles.ADMIN);
            admin.setActive(true);

            userRepository.save(admin);

            System.out.println("✅ Admin user created successfully!");
            System.out.println("   Username: admin");
            System.out.println("   Password: admin123");
        }
    }

}