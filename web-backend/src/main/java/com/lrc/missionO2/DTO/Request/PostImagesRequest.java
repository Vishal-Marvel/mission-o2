package com.lrc.missionO2.DTO.Request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PostImagesRequest {
    private List<MultipartFile> images;
}
