package com.lrc.missionO2.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "orders")
@Data
public class Order {

    @Id
    private String id = UUID.randomUUID().toString();
    private String orderNum = UUID.randomUUID().toString().substring(0, 7);
    private String user;
    private String orderStatus;
    private String locationURL;
    private String district;
    private String taluk;
    private String state;
    private List<String> images = new ArrayList<>();
    private List<OrderProduct> products = new ArrayList<>();
    private Double totalPrice;
    private Integer totalPlants;
}
