package com.lrc.missionO2.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppLoginRequestViaMobile {
    @NotBlank(message = "Mobile is mandatory")
    private String mobile;
    @NotBlank(message = "OTP is mandatory")
    private String otp;
}
