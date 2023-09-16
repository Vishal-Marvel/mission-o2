package com.lrc.missionO2.DTO.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminProfileResponse {
    private String id;
    private String name;
    private String email;

}
