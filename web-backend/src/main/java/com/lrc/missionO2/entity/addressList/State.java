package com.lrc.missionO2.entity.addressList;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Document(collection = "states")
public class State {
    @Id
    private String id = UUID.randomUUID().toString();
    private String stateName;
    private String capital;
}
