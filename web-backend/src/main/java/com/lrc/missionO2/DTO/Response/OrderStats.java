package com.lrc.missionO2.DTO.Response;

import lombok.Data;

@Data
public class OrderStats {
    private String user;
    private Integer userOrderCount;

    private String state;
    private Integer stateOrderCount;

    private String district;
    private Integer districtOrderCount;

    private String taluk;
    private Integer talukOrderCount;

    private String plant;
    private Integer plantOrderCount;


}
