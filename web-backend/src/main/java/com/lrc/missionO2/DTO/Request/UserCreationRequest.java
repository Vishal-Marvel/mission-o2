package com.lrc.missionO2.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {
    @NotBlank(message = "Mobile is mandatory")
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "Enter Valid Email")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8)
    private String password;
}
