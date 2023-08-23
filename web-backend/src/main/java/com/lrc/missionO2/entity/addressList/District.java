package com.lrc.missionO2.entity.addressList;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "districts")
public class District {
    @Id
    private String id = UUID.randomUUID().toString();
    private String districtName;
    private List<String> taluks = new ArrayList<>();
    @DBRef
    private State state;

}
