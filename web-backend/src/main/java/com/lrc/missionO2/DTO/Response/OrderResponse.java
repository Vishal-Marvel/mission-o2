package com.lrc.missionO2.DTO.Response;

import com.lrc.missionO2.entity.OrderProduct;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder

public class OrderResponse {
    private String id;
    private String orderNum;
    private String user;
    private Date orderDate;
    private String userId;
    private String orderStatus;
    private String locationURL;
    private String district;
    private String taluk;
    private String state;
    private List<byte[]> images;
    private List<OrderProduct> products;
    private Double totalPrice;
    private Integer totalPlant;
}
