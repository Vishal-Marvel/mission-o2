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
    private String orderNum;
    private String user;
    private Date orderDate;
    private String orderStatus;
    private String locationURL;
    private String district;
    private String taluk;
    private String state;
    private Address address;
    private List<String> images = new ArrayList<>();
    private List<String> postImages = new ArrayList<>();
    private List<OrderProduct> products = new ArrayList<>();
    private Double totalPrice;
    private Integer totalPlants;
    private String approvedBy;
}
