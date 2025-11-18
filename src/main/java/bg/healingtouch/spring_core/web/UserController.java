package bg.healingtouch.spring_core.web;

import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.service.UserService;
import bg.healingtouch.spring_core.web.dto.ChangePasswordDto;
import bg.healingtouch.spring_core.web.dto.RegisterDto;
import bg.healingtouch.spring_core.web.dto.UpdateUserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterDto dto) {
        User created = userService.registerNewUser(dto);
        return ResponseEntity.ok(created);
    }

    public ResponseEntity<User> updateProfile (
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserDto dto) {

        User updated = userService.updateProfile(id, dto.getFirstname(), dto.getLastname(), dto.getProfilePictures());
        return ResponseEntity.ok(updated);
    }

    //switch Status from Active to Inactive
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> switchStatus(@PathVariable UUID id) {
        userService.switchStatus(id);
        return ResponseEntity.noContent().build();
    }

    //switch role
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> switchRole(@PathVariable UUID id) {
        userService.switchRole(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordDto dto) {

        userService.changePassword(id, dto.getOldPassword(), dto.getNewPassword());
        return ResponseEntity.noContent().build();
    }

}
