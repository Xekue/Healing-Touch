package bg.healingtouch.spring_core.web.api;

import bg.healingtouch.spring_core.therapist.service.TherapistService;
import bg.healingtouch.spring_core.web.dto.AddTherapistDto;
import bg.healingtouch.spring_core.web.dto.TherapistResponseDto;
import bg.healingtouch.spring_core.web.dto.UpdateTherapistDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/therapists")
@RequiredArgsConstructor
public class TherapistApiController {

    private final TherapistService therapistService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TherapistResponseDto> addTherapist(@Valid @RequestBody AddTherapistDto dto) {
        TherapistResponseDto responseDto = therapistService.addTherapist(dto);
        return ResponseEntity.ok(responseDto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TherapistResponseDto> getTherapistById(@PathVariable UUID id) {
        TherapistResponseDto therapist = therapistService.getTherapistById(id);
        return ResponseEntity.ok(therapist);
    }

    //ADMIN can update any, therapist can update own profile
    @PreAuthorize("hasRole('ADMIN') or hasRole('THERAPIST')")
    @PutMapping("/{id}")
    public ResponseEntity<TherapistResponseDto> updateTherapist(@PathVariable UUID id, @Valid @RequestBody UpdateTherapistDto dto) {

        TherapistResponseDto updated = therapistService.updateTherapist(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ADMIN can delete ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTherapist(@PathVariable UUID id) {
        therapistService.deleteTherapist(id);
        return ResponseEntity.noContent().build();
    }

    // ADMIN can TOGGLE ACTIVE
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TherapistResponseDto> toggleActiveStatus(@PathVariable UUID id) {
        TherapistResponseDto updated = therapistService.toggleActiveStatus(id);
        return ResponseEntity.ok(updated);
    }

}
