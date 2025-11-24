package bg.healingtouch.spring_core.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactFormDto {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private String phone;

    @NotBlank
    private String subject;

    @NotBlank
    private String message;
}
