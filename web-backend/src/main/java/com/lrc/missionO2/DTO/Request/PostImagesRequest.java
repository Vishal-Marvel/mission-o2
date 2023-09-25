package com.lrc.missionO2.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostImagesRequest {
    @NotBlank(message = "Images are mandatory")

    private List<MultipartFile> images;
}
