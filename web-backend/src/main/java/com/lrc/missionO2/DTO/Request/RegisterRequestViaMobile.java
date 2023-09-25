package com.lrc.missionO2.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RegisterRequestViaMobile {
    @NotBlank(message = "Mobile Number is mandatory")
    @Size(min = 10, max = 10, message = "Mobile number should have 10 digits")
    private String mobile;
}
