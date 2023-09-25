package com.lrc.missionO2.DTO.Request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangeAdminAssistPasswordRequest {
    @NotBlank(message = "ID is mandatory")

    private String id;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8)
    private String password;
}
