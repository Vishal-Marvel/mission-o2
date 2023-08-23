package com.lrc.missionO2.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * A class that represents an authentication request with a username or email and a password.
 * The fields are annotated with @NotBlank to ensure that they are not null or empty..
 */
@Data

public class AuthenticationRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    public AuthenticationRequest(String mail, String password123) {
        this.email = mail;
        this.password = password123;
    }

    public AuthenticationRequest() {

    }
}
