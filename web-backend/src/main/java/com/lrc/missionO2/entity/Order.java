package com.lrc.missionO2.entity;

import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Order {
    @Id
    private String id = UUID.randomUUID().toString();
    private String orderNum = UUID.randomUUID().toString().substring(0, 7);
    private String user;
    private String orderStatus;
    private String locationURL;
    private Address address;
    private List<String> images;
    private Map<String, Integer> products;
}
