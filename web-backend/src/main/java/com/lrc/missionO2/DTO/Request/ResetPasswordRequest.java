package com.lrc.missionO2.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
/**
 * A class representing a Reset Password request.
 * This class encapsulates the information required to reset a user's password.
 */
@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Email is mandatory")
    @Email
    private String email;
}
