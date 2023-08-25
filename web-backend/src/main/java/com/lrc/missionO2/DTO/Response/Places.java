package com.lrc.missionO2.DTO.Response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Places {
    private List<String> places;

}
