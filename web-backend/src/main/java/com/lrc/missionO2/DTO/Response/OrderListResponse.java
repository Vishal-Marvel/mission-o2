package com.lrc.missionO2.DTO.Response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class OrderListResponse {
    private String id;
    private String orderNum;
    private Integer totalPlant;
    private String state;
    private String district;
    private Date orderDate;
    private String taluk;
    private String orderStatus;
    private Double totalPrice;
}
