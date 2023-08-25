package com.lrc.missionO2.DTO.Request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PlantRequest {
    private String name;
    private Float plantPrice;
    private Float seedPrice;
    private MultipartFile image;

}
