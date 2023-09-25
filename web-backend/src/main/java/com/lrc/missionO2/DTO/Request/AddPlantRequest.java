package com.lrc.missionO2.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AddPlantRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;
    private Float plantPrice;
    private Float seedPrice;
    @NotBlank(message = "Image is mandatory")
    private List<MultipartFile> images;

}
