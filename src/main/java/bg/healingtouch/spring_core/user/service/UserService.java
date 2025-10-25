package bg.healingtouch.spring_core.user.service;

import bg.healingtouch.spring_core.exception.DomainException;
import bg.healingtouch.spring_core.security.AuthenticationMetadata;
import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.model.UserRoles;
import bg.healingtouch.spring_core.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(String username, String email, String password, String firstname, String lastname) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setRole(UserRoles.CUSTOMER);
        user.setActive(true);
        user.setCreatedOn(LocalDateTime.now());
        user.setUpdatedOn(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("User with email {} registered successfully", user.getEmail());
        return savedUser;
    }

    public User getById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + userId + " not found"));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateProfile(UUID userId, String firstName, String lastName, String profilePicture) {
        User user = getById(userId);

        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setProfilePicture(profilePicture);
        user.setUpdatedOn(LocalDateTime.now());

        User updated =  userRepository.save(user);
        log.info("User with id {} updated successfully", updated.getId());
        return updated;
    }

    //Activation / Deactivation
    @Transactional
    public void switchStatus(UUID userId) {
        User user = getById(userId);
        user.setActive(!user.isActive());
        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);

        log.info("Switched status for user ID: {}. Now active = {}", userId, user.isActive());
    }

    @Transactional
    public void switchRole(UUID userId) {
        User user = getById(userId);
        if (user.getRole() ==  UserRoles.CUSTOMER) {
            user.setRole(UserRoles.ADMIN);
        } else  {
            user.setRole(UserRoles.CUSTOMER);
        }
        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);

        log.info("Switched role for user ID: {}. New role = {}", userId, user.getRole());
    }

    @Transactional
    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = getById(userId);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password changed for user ID: {}", userId);
    }

    // Всеки пък, когато потребител се логва, Spring Security ще извиква този метод
    // за да вземе детайлите на потребителя с този username
    // Test 1: When user exist - then return new AuthenticationMetadata
    // Test 2: When User does not exist - then throws exception
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new DomainException("User with this username does not exist."));

        return new AuthenticationMetadata(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }


}
