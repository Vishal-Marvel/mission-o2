package com.lrc.missionO2.DTO.Response;

import com.lrc.missionO2.entity.Address;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class OrderListResponse {
    private String id;
    private String orderNum;
    private Integer totalPlant;
    private Date orderDate;
    private String state;
    private String district;
    private String taluk;
    private String orderStatus;
    private String approvedBy;
    private Double totalPrice;
}
