package com.lrc.missionO2.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents an authentication response that contains a token and a role for a user.
 * It uses the @Data and @Builder annotations from Lombok to generate getters, setters, constructors, and builders for the fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String role;
}
