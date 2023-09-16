package com.lrc.missionO2.DTO.Response;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PlaceCount {
    private String _id;
    private Integer count;
}
