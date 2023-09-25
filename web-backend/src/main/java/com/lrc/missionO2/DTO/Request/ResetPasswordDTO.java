package com.lrc.missionO2.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
/**
 * A class representing a Reset Password data transfer object (DTO).
 * This class encapsulates the information required to reset a user's password.
 */
@Data
public class ResetPasswordDTO {
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must have a minimum of size 8")
    private String newPassword;
}
