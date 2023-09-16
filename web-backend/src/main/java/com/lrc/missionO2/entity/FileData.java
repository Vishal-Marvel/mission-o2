package com.lrc.missionO2.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "files")
public class FileData {
    @Id
    private String id = UUID.randomUUID().toString();
    private String fileName;
    private byte[] data;
}
