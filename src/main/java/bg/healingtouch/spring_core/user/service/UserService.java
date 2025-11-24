package bg.healingtouch.spring_core.user.service;

import bg.healingtouch.spring_core.booking.service.BookingService;
import bg.healingtouch.spring_core.exception.DomainException;
import bg.healingtouch.spring_core.security.AuthenticationMetadata;
import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.model.UserRoles;
import bg.healingtouch.spring_core.user.repository.UserRepository;
import bg.healingtouch.spring_core.web.dto.RegisterDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);


    @Transactional
    public User registerNewUser(RegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username is already taken. Please choose another.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered. Please use a different one.");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRole(UserRoles.CUSTOMER);
        user.setActive(true);
        user.setCreatedOn(LocalDateTime.now());
        user.setUpdatedOn(LocalDateTime.now());

        User saved = userRepository.save(user);
        log.info("User '{}' registered successfully.", saved.getUsername());
        return saved;
    }

    public User getById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + userId + " not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateProfile(UUID userId, String firstName, String lastName, String profilePicture) {
        User user = getById(userId);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setProfilePicture(profilePicture);
        user.setUpdatedOn(LocalDateTime.now());

        User updated = userRepository.save(user);
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
        if (user.getRole() == UserRoles.CUSTOMER) {
            user.setRole(UserRoles.ADMIN);
        } else {
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new DomainException("User with this username does not exist."));

        return new AuthenticationMetadata(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }

    public List<User> getAllCustomers() {
        return userRepository.findAllByRole(UserRoles.CUSTOMER);
    }
}
