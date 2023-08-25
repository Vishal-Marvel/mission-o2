package com.lrc.missionO2.DTO.Request;

import lombok.Data;

@Data
public class AppLoginRequest {
    private String mobile;
    private String otp;
}
