package com.elitefolk.authservice.dtos;

import com.elitefolk.authservice.security.models.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginSuccessDto {
    private String userId;
    private String userName;
    private String email;
    private String phone;
    private String role;
    private String firstName;
    private String lastName;

    public static LoginSuccessDto from(UserDetails userDetails) {
        CustomUserDetails details = (CustomUserDetails) userDetails;
        return new LoginSuccessDto(
                details.getId().toString(),
                details.getUsername(),
                details.getEmail(),
                details.getMobile(),
                details.getRole(),
                details.getFirstName(),
                details.getLastName()
        );
    }
}
