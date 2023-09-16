package com.lrc.missionO2.DTO.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResponse {
    private byte[] image;
    private String fileName;
}
