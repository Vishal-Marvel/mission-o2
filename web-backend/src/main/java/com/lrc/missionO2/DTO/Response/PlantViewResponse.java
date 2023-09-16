package com.lrc.missionO2.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantViewResponse {
    private String id;
    private String name;
    private Float seedPrice;
    private Float plantPrice;
    private List<String> images;
}
