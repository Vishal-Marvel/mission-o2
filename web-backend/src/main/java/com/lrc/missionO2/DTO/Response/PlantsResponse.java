package com.lrc.missionO2.DTO.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class PlantsResponse {
    private String id;
    private byte[] image;
}
