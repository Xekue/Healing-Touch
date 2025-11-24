package bg.healingtouch.spring_core.therapist.service;

import bg.healingtouch.spring_core.booking.service.BookingService;
import bg.healingtouch.spring_core.therapist.model.Therapist;
import bg.healingtouch.spring_core.therapist.repository.TherapistRepository;
import bg.healingtouch.spring_core.user.model.User;
import bg.healingtouch.spring_core.user.model.UserRoles;
import bg.healingtouch.spring_core.user.repository.UserRepository;
import bg.healingtouch.spring_core.web.dto.AddTherapistDto;
import bg.healingtouch.spring_core.web.dto.TherapistResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TherapistService {

    private final TherapistRepository therapistRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    @CacheEvict(value = "therapists", allEntries = true)
    public TherapistResponseDto addTherapist(AddTherapistDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Therapist therapist = new Therapist();
        therapist.setUser(user);

        therapist.setFirstName(user.getFirstName());
        therapist.setLastName(user.getLastName());

        therapist.setProfilePicture("/images/default-therapist.png");

        therapist.setTitle("Certified Massage Therapist");
        therapist.setBio("Professional massage therapist dedicated to providing exceptional wellness services.");

        therapist.setSkills(List.of("Massage Therapy"));
        therapist.setSpecialties(List.of("Relaxation Massage"));
        therapist.setCertifications(List.of("Certified Practitioner"));
        therapist.setLanguages(List.of("English", "Russian"));

        therapist.setAvailability("9:00 AM - 9:00 PM Daily");
        therapist.setLocation("Ruse, Bulgaria");

        therapist.setExperienceYears(ThreadLocalRandom.current().nextInt(1, 11));
        therapist.setHourlyRate(dto.getHourlyRate());
        therapist.setActive(true);

        therapist.setTotalSessions(0);
        therapist.setRating(0.0);

        therapistRepository.save(therapist);

        return mapToResponseDto(therapist);
    }

    public TherapistResponseDto getTherapistById(UUID id) {
        Therapist therapist = therapistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Therapist not found"));
        return mapToResponseDto(therapist);
    }

    public List<TherapistResponseDto> findAllActiveTherapists() {
        log.info("Fetching active & non-deleted therapists...");
        return therapistRepository.findAllByActiveTrueAndDeletedFalse()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public List<TherapistResponseDto> getAllTherapists() {
        return therapistRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public TherapistResponseDto updateTherapist(UUID id, @Valid TherapistResponseDto dto) {
        Therapist therapist = therapistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Therapist not found"));

        if (dto.getSkills() != null) therapist.setSkills(dto.getSkills());
        if (dto.getExperienceYears() != null) therapist.setExperienceYears(dto.getExperienceYears());
        if (dto.getHourlyRate() != null) therapist.setHourlyRate(dto.getHourlyRate());
        if (dto.getProfilePicture() != null) therapist.setProfilePicture(dto.getProfilePicture());
        if (dto.getActive() != null) therapist.setActive(dto.getActive());

        therapistRepository.save(therapist);
        return mapToResponseDto(therapist);
    }

    @Transactional
    public void softDeleteTherapist(UUID therapistId) {
        Therapist therapist = therapistRepository.findById(therapistId)
                .orElseThrow(() -> new EntityNotFoundException("Therapist not found"));

        User user = therapist.getUser();

        therapist.setActive(false);
        therapist.setDeleted(true);
        therapistRepository.save(therapist);

        user.setRole(UserRoles.CUSTOMER);
        userRepository.save(user);
    }

    public TherapistResponseDto toggleActiveStatus(UUID id) {
        Therapist therapist = therapistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Therapist not found"));

        if (therapist.isDeleted()) {
            throw new IllegalStateException("Cannot toggle status of a deleted therapist.");
        }

        therapist.setActive(!therapist.isActive());
        therapistRepository.save(therapist);
        return mapToResponseDto(therapist);
    }

    public void updateTherapistProfile(UUID id, TherapistResponseDto dto) {
        Therapist therapist = therapistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Therapist not found"));

        therapist.setBio(dto.getBio());
        therapist.setSpecialties(dto.getSpecialties());
        therapist.setCertifications(dto.getCertifications());
        therapist.setLanguages(dto.getLanguages());
        therapist.setExperienceYears(dto.getExperienceYears());
        therapist.setTotalSessions(dto.getTotalSessions());
        therapist.setTitle(dto.getTitle());
        therapist.setLocation(dto.getLocation());
        therapist.setRating(dto.getRating());

        therapistRepository.save(therapist);
    }

    private TherapistResponseDto mapToResponseDto(Therapist therapist) {
        TherapistResponseDto dto = new TherapistResponseDto();

        dto.setId(therapist.getId());
        dto.setUserId(therapist.getUser().getId());
        dto.setFirstName(therapist.getUser().getFirstName());
        dto.setLastName(therapist.getUser().getLastName());
        dto.setProfilePicture(therapist.getProfilePicture());
        dto.setTitle(therapist.getTitle());
        dto.setRating(therapist.getRating());
        dto.setLocation(therapist.getLocation());
        dto.setAvailability(therapist.getAvailability());
        dto.setBio(therapist.getBio());
        dto.setSpecialties(therapist.getSpecialties());
        dto.setCertifications(therapist.getCertifications());
        dto.setLanguages(therapist.getLanguages());
        dto.setExperienceYears(therapist.getExperienceYears());
        dto.setTotalSessions(therapist.getTotalSessions() != null ? therapist.getTotalSessions() : 0);
        dto.setSkills(therapist.getSkills());
        dto.setHourlyRate(therapist.getHourlyRate());
        dto.setActive(therapist.isActive());
        dto.setDeleted(therapist.isDeleted());

        return dto;
    }

    public Optional<Therapist> getTherapistByUserId(UUID userId) {
        return therapistRepository.findByUserId(userId);
    }

    public boolean isAdmin(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole() == UserRoles.ADMIN)
                .orElse(false);
    }

    public Therapist getById(@NotNull UUID therapistId) {
        return therapistRepository.findById(therapistId)
                .orElseThrow(() -> new EntityNotFoundException("Therapist not found"));
    }

    public Therapist restoreOrCreateDefault(User user) {
        return therapistRepository.findByUserId(user.getId()).map(t -> {
            t.setDeleted(false);
            t.setActive(true);
            return therapistRepository.save(t);
        }).orElseGet(() -> {
            AddTherapistDto dto = new AddTherapistDto();
            dto.setUserId(user.getId());
            dto.setHourlyRate(BigDecimal.valueOf(40));
            return therapistRepository.findById(addTherapist(dto).getId()).orElseThrow();
        });
    }

    public Optional<Therapist> findByUserId(UUID userId) {
        return therapistRepository.findByUserId(userId);
    }
}
