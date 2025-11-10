package bg.healingtouch.spring_core.therapist.service;

import bg.healingtouch.spring_core.booking.service.BookingService;
import bg.healingtouch.spring_core.therapist.model.Therapist;
import bg.healingtouch.spring_core.therapist.repository.TherapistRepository;
import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.repository.UserRepository;
import bg.healingtouch.spring_core.web.dto.AddTherapistDto;
import bg.healingtouch.spring_core.web.dto.TherapistResponseDto;
import bg.healingtouch.spring_core.web.dto.UpdateTherapistDto;
import org.springframework.cache.annotation.Cacheable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TherapistService {

    private final TherapistRepository therapistRepository;
    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    public TherapistResponseDto addTherapist(AddTherapistDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));


        Therapist therapist = new Therapist();
        therapist.setUser(user);
        therapist.setSkills(dto.getSkills());
        therapist.setExperienceYears(dto.getExperienceYears());
        therapist.setHourlyRate(dto.getHourlyRate());
        therapist.setActive(dto.isActive());
        therapist.setProfilePicture(dto.getProfilePicture());

        therapistRepository.save(therapist);

        return mapToResponseDto(therapist);
    }

    public TherapistResponseDto getTherapistById(UUID id) {
        Therapist therapist = therapistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Therapist not found"));
        return mapToResponseDto(therapist);
    }

    public List<TherapistResponseDto> getAllTherapists() {
        return therapistRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Cacheable("therapists")
    public List<TherapistResponseDto> findAllActiveTherapists() {
        log.info("Fetching active therapists from the database...");
        return therapistRepository.findAllByActiveTrue()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public TherapistResponseDto updateTherapist(UUID id, UpdateTherapistDto dto) {
        Therapist therapist = therapistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Therapist not found"));

        if (dto.getSkills() != null) therapist.setSkills(dto.getSkills());
        if (dto.getExperienceYears() != null) therapist.setExperienceYears(dto.getExperienceYears());
        if (dto.getHourlyRate() != null) therapist.setHourlyRate(dto.getHourlyRate());
        if (dto.getProfilePicture() != null) therapist.setProfilePicture(dto.getProfilePicture());

        if (dto.getIsActive() != null) {
            therapist.setActive(dto.getIsActive());
        }

        therapistRepository.save(therapist);
        return mapToResponseDto(therapist);
    }

    // DELETE!
    public void deleteTherapist(UUID id) {
        if (!therapistRepository.existsById(id)) {
            throw new EntityNotFoundException("Therapist not found");
        }
        therapistRepository.deleteById(id);
    }

    // TOGGLE ACTIVE!
    public TherapistResponseDto toggleActiveStatus(UUID id) {
        Therapist therapist = therapistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Therapist not found"));

        therapist.setActive(!therapist.isActive());
        therapistRepository.save(therapist);

        return mapToResponseDto(therapist);
    }


    // HELPER!
    private TherapistResponseDto mapToResponseDto(Therapist therapist) {
        TherapistResponseDto dto = new TherapistResponseDto();
        dto.setId(therapist.getId());
        dto.setFirstName(therapist.getUser().getFirstName());
        dto.setLastName(therapist.getUser().getLastName());
        dto.setActive(therapist.isActive());
        return dto;
    }

    public Optional<Therapist> getTherapistByUserId(UUID userId) {
        return therapistRepository.findByUserId(userId);
    }
}
