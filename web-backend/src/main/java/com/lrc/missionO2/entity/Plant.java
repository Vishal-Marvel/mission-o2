package com.lrc.missionO2.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document(collection = "plants")
@Data
public class Plant {
    @Id
    private String id = UUID.randomUUID().toString();
    private String name;
    private String image;
    private Float seedPrice;
    private Float plantPrice;
}
