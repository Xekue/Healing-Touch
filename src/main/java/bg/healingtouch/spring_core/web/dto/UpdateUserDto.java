package bg.healingtouch.spring_core.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDto {

    private String firstname;
    private String lastname;
    private String profilePictures;
}
