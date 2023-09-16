package com.lrc.missionO2.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct {
    private String plantId;
    private String plantName;
    private String type;
    private Integer quantity;
}
