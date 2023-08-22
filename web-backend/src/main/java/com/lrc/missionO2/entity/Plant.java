package com.lrc.missionO2.entity;

import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;

public class Plant {
    @Id
    private String id = UUID.randomUUID().toString();
    private String name;
    private List<String> images;
    private Float seedPrice;
    private Float plantPrice;
}
