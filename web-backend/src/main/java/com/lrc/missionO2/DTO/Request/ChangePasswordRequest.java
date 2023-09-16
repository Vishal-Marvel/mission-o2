package com.lrc.missionO2.DTO.Request;


import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String id;
    private String password;
}
